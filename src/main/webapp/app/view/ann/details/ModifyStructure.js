Ext.define('TSP.view.ann.details.ModifyStructure', {
    extend: 'Ext.window.Window',
    alias : 'widget.annmodifystructure',

    title : 'Modify structure',
    layout: 'border',
    border: false,
    autoShow: true,
    width: 780,
    height: 500,
    modal: true,
    maximizable: true,

    // html network structure variables
    structureElementsPanelHtml: '<div id="structureElements">' +
                                '<br /><div class="dd-hidden-neuron">Neuron</div><br /><br /><div class="dd-neuron-layer">Neuron Layer</div><br /><br /><br />' +
                                '<ul class="dd-instructions"><li>Drag neurons to layers or between them</li><li>Drag neuron layers to layers container</li>' +
                                '<li><span style="color: red; font-weight: bold;">Warning:</span> structure changes will reset all neuron weights!</li></ul>' +
                                '</div>',
    neuronRemoveButtonHtml: ' <a href="#" onclick="this.parentNode.parentNode.removeChild(this.parentNode)"><img class="remove-element-img" src="images/remove_element.png" /></a>',
    layerRemoveButtonHtml: ' <a href="#" onclick="this.parentNode.parentNode.removeChild(this.parentNode)"><img class="layer-remove-element-img" src="images/remove_element.png" /></a>',

    initComponent: function() {
        this.items = [
            {
                region: 'west',
                width: 200,
                margin: '2 2 2 2',
                xtype: 'panel',
                itemId: 'structureElements',
                title: 'Structure elements',
                html: this.structureElementsPanelHtml
            },
            {
                region: 'center',
                margin: '2 2 2 0',
                xtype: 'panel',
                itemId: 'structure',
                title: 'Network structure'
            }
        ];

        this.buttons = [
            {
                text: 'Save',
                action: 'updateStructure'
            },
            {
                text: 'Cancel',
                scope: this,
                handler: this.close
            }
        ];

        this.callParent(arguments);
    },

    showWindow: function(annModel) {
        this.generateNetworkStructure(annModel);
        this.show();
    },

    /**
     * Generates DND html elements based on neural network model; elements of structure are
     * divs which represent neuron layers and neurons; neurons can be dragged and dropped to
     * layers or dragged between payers; layers can be dropped to layers container and removed
     * from it.
     *
     * @param annModel
     */
    generateNetworkStructure: function(annModel) {

        var me = this;

        // initialize drag zone for structure elements
        var structureElements = Ext.get('structureElements').select('div');
        me.initializeDragZone(structureElements, ['neurons', 'neuronLayers'], true);

        // add network structure (dnd elements and drop zones) to it's panel - dnd elements
        // and drop zones are structure elements (neurons and neuron layers)
        var structureHtmlElements = this.createNetworkStructure(annModel);
        var structurePanel = this.items.get('structure');
        structurePanel.update(structureHtmlElements);

        // initialize drop zone for neuron layers
        var layersContainer = Ext.get('layers-container');
        me.initializeNeuronLayerDropZone(layersContainer, '.layers-container', 'layers-container-drop-over', me);

        // initialize drag and drop zones for neurons; layer in layers container are neuron drop zones but also
        // drag zone, because we can drag neurons from layer to layer
        var layerElements = Ext.get('layers-container').select('div');
        Ext.each(layerElements.elements, function(element) {
            var neuronElements = Ext.get(element).select('div');
            me.initializeDragZone(neuronElements, 'neurons', false);
            me.initializeNeuronDropZone(element, '.neuronlayer', 'neuronlayer-drop-over', me);
        });

    },

    /**
     * Writes html tags of network structure consisted of divs (neuron layers) and imgs
     * (neurons) to "structure" panel
     *
     * @param annModel
     *
     * @return structureHtmlElements
     */
    createNetworkStructure: function(annModel) {

        var structureHtmlElements = '<div id="layers-container" class="layers-container">';

        var neuronLayers = annModel.neuronLayers();

        // from -1 because input layer isn't defined; it's not a layer, neurons in it are only
        // input data into network, but we're presenting it like it is an layer
        for( var i = -1; i < neuronLayers.getCount(); i++ ) {

            var neuronLayer = neuronLayers.getAt(i);
            var numNeurons = i == -1 ? annModel.get('numInputNeurons') : neuronLayer.get('numNeurons');

            // depending if we're in input, hidden or output neuron, these three variables are different
            var title = 'Hidden layer ';
            var removeButton = this.layerRemoveButtonHtml;
            var htmlNeuronClass = 'dd-hidden-neuron';
            if( i == -1 ) {
                title = 'Input layer ';
                removeButton = '';
                htmlNeuronClass = 'dd-input-neuron';
            }
            if( i == neuronLayers.getCount() - 1 ) {
                title = 'Output layer ';
                removeButton = '';
                htmlNeuronClass = 'dd-output-neuron';
            }

            // add neurons to current layer
            var htmlNeurons = '';
            for( var j = 0; j < numNeurons; j++ ) {
                htmlNeurons += '<div class="' + htmlNeuronClass + '">' + this.neuronRemoveButtonHtml + '</div>';
            }

            structureHtmlElements += '<div id="neuronlayer-' + (i+2) + '" class="neuronlayer">' + title + removeButton + htmlNeurons + '</div>';

        }

        structureHtmlElements += '</div>';

        return structureHtmlElements;

    },

    /**
     * Initializes drag zone for "elements" html elements for "dragGroup(s)"
     *
     * @param elements
     * @param dragGroups
     * @param cloneNode
     */
    initializeDragZone: function(elements, dragGroups, cloneNode) {
        var i = 0;
        Ext.each(elements.elements, function(element) {

            element.dragZone = Ext.create('Ext.dd.DragZone', element, {

                ddGroup: (dragGroups.constructor.toString().indexOf('Array') == -1) ? dragGroups : dragGroups[i],

                getDragData: function(e) {
                    var sourceEl = e.getTarget(), dragEl;
                    if (sourceEl) {
                        if( cloneNode ) {
                            dragEl = sourceEl.cloneNode(true);
                        }
                        else {
                            dragEl = sourceEl;
                        }
                        dragEl.id = Ext.id();
                        return element.dragData = {
                            sourceEl: sourceEl,
                            repairXY: Ext.fly(sourceEl).getXY(),
                            ddel: dragEl
                        };
                    }
                },

                getRepairXY: function() {

                    return this.dragData.repairXY;
                }
            });
            i++;
        });
    },

    /**
     * Initializes drop zones for neurons; neuron drop zones are layers (class neuronlayer)
     *
     * @param element
     * @param dropTarget
     * @param hoverCls
     * @param me
     */
    initializeNeuronDropZone: function(element, dropTarget, hoverCls, me) {
        element.dropZone = Ext.create('Ext.dd.DropZone', element, {

            ddGroup: 'neurons',

            getTargetFromEvent: function(e) {
                return e.getTarget(dropTarget);
            },

            onNodeEnter : function(target, dd, e, data){
                Ext.fly(target).addCls(hoverCls);
            },

            onNodeOut : function(target, dd, e, data){
                Ext.fly(target).removeCls(hoverCls);
            },

            onNodeOver : function(target, dd, e, data){
                return Ext.dd.DropZone.prototype.dropAllowed;
            },

            onNodeDrop : function(target, dd, e, data){
                var targetEl = Ext.get(target);

                var className = 'dd-hidden-neuron';
                if( targetEl.dom.innerHTML.indexOf('Input') >= 0 ) {
                    className = 'dd-input-neuron';
                }
                if( targetEl.dom.innerHTML.indexOf('Output') >=0 ) {
                    className = 'dd-output-neuron';
                }

                var neuron = data.ddel;
                neuron.className = className;
                neuron.innerHTML = me.neuronRemoveButtonHtml;

                targetEl.appendChild(neuron);

                var neuronElements = targetEl.select('div');
                me.initializeDragZone(neuronElements, 'neurons', false);

                return true;
            }
        });
    },

    /**
     * Initializes drop zone for neuron layers (div element which occupies center panel, class layers-container)
     *
     * @param element
     * @param dropTarget
     * @param hoverCls
     * @param me
     */
    initializeNeuronLayerDropZone: function(element, dropTarget, hoverCls, me) {
        element.dropZone = Ext.create('Ext.dd.DropZone', element, {

            ddGroup: 'neuronLayers',

            getTargetFromEvent: function(e) {
                return e.getTarget(dropTarget);
            },

            onNodeEnter : function(target, dd, e, data){
                Ext.fly(target).addCls(hoverCls);
            },

            onNodeOut : function(target, dd, e, data){
                Ext.fly(target).removeCls(hoverCls);
            },

            onNodeOver : function(target, dd, e, data){
                return Ext.dd.DropZone.prototype.dropAllowed;
            },

            onNodeDrop : function(target, dd, e, data){
                var targetEl = Ext.get(target);

                var layer = document.createElement('div');
                layer.innerHTML = 'Hidden layer ' + me.layerRemoveButtonHtml;
                layer.className = 'neuronlayer';

                targetEl.dom.insertBefore(layer, targetEl.dom.lastChild);

                me.initializeNeuronDropZone(layer, '.neuronlayer', 'neuronlayer-drop-over', me);

                return true;
            }
        });
    },

    /**
     * Loops over structure's DOM and returns number of neurons per each layer
     * in string format; numbers are separated by one space
     */
    getModifiedStructure: function() {
        var structure = '';
        var layerElements = Ext.get('layers-container').select('div');
        Ext.each(layerElements.elements, function(layerElement) {
            var neuronElements = Ext.get(layerElement).select('div');
            if( neuronElements.getCount() > 0 ) {
                structure += neuronElements.getCount() + ' ';
            }
        });
        return structure.trim();
    }

});