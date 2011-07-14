Ext.define('TSP.view.ann.drawing.AnnGraph', {
    extend: 'Ext.draw.Component',
    alias: 'widget.anngraph',

    viewBox: false,

    X_STEP: 180,
    Y_START: 100,
    Y_STEP: 120,
    NEURON_RADIUS: 15,

    networkInputs: null,

    /**
     * Draws the neural network structure from selected neural network model
     *
     * @param annModel
     */
    drawGraph: function(annModel, panelWidth) {

        var surface = this.surface;

        // clear surface
        surface.removeAll(true);

        // draw header
        this.drawHeader(annModel, panelWidth, surface);

        var annDrawingStructure = this.createAnnDrawingStructure(annModel, panelWidth);

        // draw neural network structure
        for( var i = 0; i < annDrawingStructure.length; i++) {
            for( var j = 0; j < annDrawingStructure[i].length; j++) {

                // draw neuron sprite
                var neuronStructure = annDrawingStructure[i][j];
                var neuronSprite = Ext.create('TSP.view.ann.drawing.NeuronSprite', Ext.apply({},
                                   {x: neuronStructure.x, y: neuronStructure.y, radius: neuronStructure.radius, style: {'stroke-width': "1.5", stroke: 'black', fill: neuronStructure.fillColor}},
                                   { surface: surface }));
                surface.add(neuronSprite).show(true);

                // draw neuron activation, but only if inputs are provided - if network is being tested
                if( this.networkInputs != null ) {
                    var neuronActivationSprite = this.drawNeuronActivations(surface, neuronStructure.activation, neuronStructure.x, neuronStructure.y, neuronStructure.radius);
                    surface.add(neuronActivationSprite).show(true);
                }

                // draw weight sprites - but only if we're not drawing the input layer
                var neuronWeights = neuronStructure.neuronWeights;
                if( neuronWeights.length > 0 ) {
                    var previousNeuronLayer = annDrawingStructure[i-1];
                    this.drawWeightsLinesAndText(surface, neuronWeights, previousNeuronLayer, neuronStructure);
                }

            }
        }

        // reset networkInputs because activations aren't show when a network is selected from list, only when user tests network - provides inputs
        this.networkInputs = null;

    },

    /**
     * Draws graph header with main header text and header text and lines for all layers
     *
     * @param annModel
     * @param panelWidth
     * @param surface
     */
    drawHeader: function(annModel, panelWidth, surface) {

        var headerText = '"' + annModel.get('name') + '" STRUCTURE GRAPH';
        var headerTextWidth = Ext.create('Ext.util.TextMetrics').getWidth(headerText);
        var headerTextX = (panelWidth / 2) - (headerTextWidth / 1.4);

        // draw main header text
        surface.add(
            {
                type: 'text',
                fill: '#000',
                font: '12px Arial Black',
                text: headerText,
                'font-weight': 'bold',
                x: headerTextX,
                y: 20
            }
        ).show(true);

        var numHiddenLayers = annModel.get('numHiddenLayers');
        var networkWidth = this.getNetworkGraphWidth(numHiddenLayers);
        var beginX = (panelWidth / 2) - (networkWidth / 2);

        // define text and position of all layers headers
        var layersHeaders = [
            {
                text: 'input layer',
                x: beginX - (Ext.create('Ext.util.TextMetrics').getWidth('input layer') / 2)
            },
            {
                text: 'hidden layer',
                x: beginX + (networkWidth / 2) - (Ext.create('Ext.util.TextMetrics').getWidth('hidden layer') / 2) - this.NEURON_RADIUS
            },
            {
                text: 'output layer',
                x: (beginX + networkWidth) - (Ext.create('Ext.util.TextMetrics').getWidth('output layer') / 2) - (this.NEURON_RADIUS * 2)
            }
        ];

        // define paths for all layers header lines
        var layersHeadersLines = [
            {
                x1: beginX - (this.NEURON_RADIUS * 2),
                y1: 70,
                x2: beginX - (this.NEURON_RADIUS * 2),
                y2: 65,
                x3: beginX + (this.NEURON_RADIUS * 2),
                y3: 65,
                x4: beginX + (this.NEURON_RADIUS * 2),
                y4: 70
            },
            {
                x1: (beginX + this.X_STEP) - (this.NEURON_RADIUS * 2),
                y1: 70,
                x2: (beginX + this.X_STEP) - (this.NEURON_RADIUS * 2),
                y2: 65,
                x3: (beginX + this.X_STEP + ((numHiddenLayers - 1) * this.X_STEP)) + (this.NEURON_RADIUS * 2),
                y3: 65,
                x4: (beginX + this.X_STEP + ((numHiddenLayers - 1) * this.X_STEP)) + (this.NEURON_RADIUS * 2),
                y4: 70
            },
            {
                x1: (beginX + networkWidth) - (this.NEURON_RADIUS * 4),
                y1: 70,
                x2: (beginX + networkWidth) - (this.NEURON_RADIUS * 4),
                y2: 65,
                x3: (beginX + networkWidth),
                y3: 65,
                x4: (beginX + networkWidth),
                y4: 70
            }
        ];

        // draw headers texts and lines
        for( var i = 0; i < 3; i++ ) {
            if( numHiddenLayers == 0 && i == 1 ) {
                continue;
            }
            surface.add(
                {
                    type: 'text',
                    fill: '#000',
                    font: '10px Arial Black',
                    'font-weight': 'bold',
                    text: layersHeaders[i].text,
                    x: layersHeaders[i].x,
                    y: 50
                }
            ).show(true);
            surface.add(
                {
                    type: 'path',
                    stroke: '#000',
                    'stroke-width': 1,
                    path: 'M' + layersHeadersLines[i].x1 + "," + layersHeadersLines[i].y1 + "L" + layersHeadersLines[i].x2 + "," + layersHeadersLines[i].y2 +
                          'L' + layersHeadersLines[i].x3 + "," + layersHeadersLines[i].y3 + "L" + layersHeadersLines[i].x4 + "," + layersHeadersLines[i].y4
                }
            ).show(true);
        }

    },

    /**
     * Loops through weights of currently drawn neuron and draws weight values (text) and weight lines
     * from all neurons in previous layer to current neuron
     *
     * @param neuronWeights
     * @param previousNeuronLayer
     * @param currentNeuronStructure
     */
    drawWeightsLinesAndText: function(surface, neuronWeights, previousNeuronLayer, currentNeuronStructure) {

        for( var i = 0; i < neuronWeights.length; i++) {

            // modify from and to x-es for the value of radius so that weight line starts/ends
            // from/at border of neuron sprite, not it's circle center (at which by default x and y point)
            var fromNeuron = previousNeuronLayer[i];
            fromNeuron.x += fromNeuron.radius;
            var toNeuron = currentNeuronStructure;
            toNeuron.x -= toNeuron.radius;

            // calculate angle between first and second weight line point and covert it to degrees
            var angle = Math.atan2(toNeuron.y - fromNeuron.y, toNeuron.x - fromNeuron.x);
            angle *= (180 / Math.PI);

            // weight text, or value
            var weightText = neuronWeights[i].toFixed(6);

            // here we define position of weight/s text - it will be located at the right end of weight line
            // (1.3 divider - 2 would put it at the center of the line, 5 at the left end for example)
            // with x and y on the line; because text rotates around it's center, it's x coordinate must be
            // corrected by half of it's width; in that way, we put text's center on the line and rotation
            // places it tight along the line. Also we have additional two small corrections (x: 4, y: 8) to
            // separate text from the line just a little bit
            var textWidthCorrection = Ext.create('Ext.util.TextMetrics').getWidth(weightText) / 2;
            var weightTextX = ((toNeuron.x - fromNeuron.x) / 1.3) + fromNeuron.x - textWidthCorrection + 4;
            var weightTextY = ((toNeuron.y - fromNeuron.y) / 1.3) + fromNeuron.y + 8;

            // define weight text and weight line sprites
            var weightTextSprite = Ext.create('TSP.view.ann.drawing.NeuronWeightTextSprite', Ext.apply({},
                                   {x: weightTextX, y: weightTextY, text: weightText, rotate: {degrees: angle} },
                                   { surface: surface }));
            var weightSprite = Ext.create('TSP.view.ann.drawing.NeuronWeightSprite', Ext.apply({},
                               {fromNeuron: fromNeuron, toNeuron: toNeuron, weightText: weightTextSprite},
                               { surface: surface }));

            // draw sprites
            surface.add(weightSprite).show(true);
            surface.add(weightTextSprite).show(true);

            // reset orig neuron sprite x and y to avoid incrementation by radius value at every loop
            fromNeuron.x -= fromNeuron.radius;
            toNeuron.x += fromNeuron.radius;
        }

    },

    /**
     * Draws neurons activations on their tops when user tests network - feeds inputs
     *
     * @param surface
     * @param activation
     * @param neuronX
     * @param neuronY
     * @param neuronRadius
     */
    drawNeuronActivations: function(surface, activation, neuronX, neuronY, neuronRadius) {
        var activationWidth = Ext.create('Ext.util.TextMetrics').getWidth(activation);
        var x = neuronX - ((activationWidth - 5) / 2);
        var y = neuronY - neuronRadius - 8;
        var activationTextSprite = Ext.create('TSP.view.ann.drawing.NeuronActivationTextSprite', Ext.apply({},
                                   {x: x, y: y, text: activation },
                                   { surface: surface }));
        return activationTextSprite;
    },

    /**
     * Returns neural network structure prepared for drawing, which consists of arrays of neuron
     * structures (each array of neuron structures represents a neuron layer) which hold coordinates,
     * radius, fill color and weight values for each neuron
     *
     * @param annModel
     *
     * @return neuronLayers
     */
    createAnnDrawingStructure: function(annModel, panelWidth) {

        // array which holds all neurons sorted by layers of network - or it's structure - returned by this function
        var neuronLayers = new Array();

        // get number of neurons in layer which has most of them - we need this for vertical positioning
        // of neurons per layers so that they can be centered according to biggest layer
        var maxNumNeurons = this.getNumNeuronsInBiggestLayer(annModel);

        // x coordinates of neurons - increments per neuron layer
        // - first x coordinate - x coordinate of neurons in input layer - is calculated as subtraction
        //   of panel and network widths to center the network structure drawing according to panel
        var networkWidth = this.getNetworkGraphWidth(annModel.get('numHiddenLayers'));
        var x = (panelWidth / 2) - (networkWidth / 2);

        // neural network layer models
        var neuronLayerModels = annModel.neuronLayers();

        // loop through all layers of network including input layer which is not defined as layer in model
        // - thus -1 as i's start value
        for( var i = -1; i < neuronLayerModels.getCount(); i++ ) {

            // numNeurons is defined differently if we're in input layer
            var neuronLayerModel = neuronLayerModels.getAt(i);
            var numNeurons = i == -1 ? annModel.get('numInputNeurons') : neuronLayerModel.get('numNeurons');

            // colors of neuron sprites in input, hidden and ouput layers
            var fillColor = 'blue';
            if( i == -1 ) {
                fillColor = 'red';
            }
            if( i == neuronLayerModels.getCount() - 1 ) {
                fillColor = 'green';
            }

            // fill array with structures of this layer's neurons
            var neurons = this.createNeuronsDrawingStructures(neuronLayerModel, maxNumNeurons, numNeurons, x, fillColor);

            // add new layer of neurons and increase x coordinate for next layer's neurons
            neuronLayers.push(neurons);

            x += this.X_STEP;
        }

        return neuronLayers;

    },

    /**
     * Creates neuron structures for current layer; called by "createAnnDrawingStructure"
     *
     * @param neuronLayerModel
     * @param maxNumNeurons
     * @param numNeurons
     * @param x
     * @param fillColor
     *
     * @return neurons
     */
    createNeuronsDrawingStructures: function(neuronLayerModel, maxNumNeurons, numNeurons, x, fillColor) {

        // array which holds neuron structure for drawing; return value of this function
        var neurons = new Array();

        // y coordinate of first neuron in current layer - it's defined as Y_START constant plus
        // difference between biggest(max) layer and current layer heights divided at half; in this
        // way, we "center" all layers according to biggest layer
        // - it increments per neuron, and resets for next layer's neurons
        var y = ((maxNumNeurons * this.Y_STEP) - (numNeurons * this.Y_STEP)) / 2;
        y += this.Y_START;

        // loop through all neurons of current layer and fill "neuron" structure with appropriate data
        for( var i = 0; i < numNeurons; i++ ) {

            var neuronWeights = new Array();

            // if we're at input layer, "neuronLayerModel" will be undefined because input layer isn't stored in DB,
            // it has only initial inputs, which aren't outputs from neurons, and those inputs don't have weights
            var activation = '';
            if( neuronLayerModel != undefined ) {
                var neuronModel = neuronLayerModel.neurons().getAt(i);
                activation = neuronModel.get('activation').toFixed(6);
                neuronWeights = this.getNeuronsWeightValues(neuronModel);
            }
            else {
                // if we're at input layer and network is being tested, inputs to network are input neurons' "activations"
                if( this.networkInputs != null ) {
                    activation = Number(this.networkInputs[i]);
                    if( this.networkInputs[i].length > 6 ) {
                        activation = activation.toFixed(6);
                    }
                }
            }

            // neuron structure which holds data for drawing neuron sprite
            var neuron = {
                x: x,
                y: y,
                radius: this.NEURON_RADIUS,
                fillColor: fillColor,
                activation: activation,
                neuronWeights: neuronWeights
            };

            // add new structure and increase y coordinate for next neuron of this layer
            neurons.push(neuron);

            y +=this.Y_STEP;

        }

        return neurons;

    },

    /**
     * Adds weight values to each neuron structure
     *
     * @param neuronModel
     *
     * @return neuronWeights
     */
    getNeuronsWeightValues: function(neuronModel) {

        var neuronWeights = new Array();

        var neuronWeightModels = neuronModel.neuronWeights();

        for( var i = 0; i < neuronModel.get('numInputs'); i++ ) {
            var neuronWeightModel = neuronWeightModels.getAt(i);
            neuronWeights.push(neuronWeightModel.get('weight'));
        }

        return neuronWeights;
    },

    /**
     * Calculates network structure graph width by counting num. of layers and multiplying that number with
     * X_STEP - distance between layers
     *
     * @param numHiddenLayers
     *
     * @return networkWidth
     */
    getNetworkGraphWidth: function(numHiddenLayers) {
        var networkWidth = ((numHiddenLayers + 1) * this.X_STEP) + (this.NEURON_RADIUS * 2);
        return networkWidth;
    },

    /**
     * Calculates network structure graph height by counting num. of neurons in biggest layer, multiplying
     * it with Y_STEP (distance between neurons at y coordinate) and adding header height (Y_START)
     *
     * @param annModel
     *
     * @return networkHeight
     */
    getNetworkGraphHeight: function(annModel) {
        var maxNumNeurons = this.getNumNeuronsInBiggestLayer(annModel);
        var networkHeight = this.Y_START + ((maxNumNeurons - 1) * this.Y_STEP) + this.NEURON_RADIUS + 10;
        return networkHeight;
    },

    /**
     * Finds biggest layer and returns num. of neurons in it
     *
     * @param annModel
     *
     * @return maxNumNeurons
     */
    getNumNeuronsInBiggestLayer: function(annModel) {
        var neuronsPerLayer = new Array();

        Ext.each(annModel, function(neuralNetwork) {
            neuronsPerLayer.push(neuralNetwork.get('numInputNeurons'));
            neuralNetwork.neuronLayers().each(function(neuronLayer) {
                neuronsPerLayer.push(neuronLayer.get('numNeurons'));
            });
        });

        var maxNumNeurons = 0;
        neuronsPerLayer.forEach(function(i) {
            if( i >= maxNumNeurons ) {
                maxNumNeurons = i;
            }
        });

        return maxNumNeurons;
    },

    /**
     * This is called only when network is being tested - network inputs are input neurons' "activations" (outputs)
     *
     * @param networkInputs
     */
    setNetworkInputs: function(networkInputs) {
        this.networkInputs = networkInputs;
    }

});

Ext.define('TSP.view.ann.drawing.NeuronSprite', {
    extend: 'Ext.draw.Sprite',
    alias: 'widget.neuronsprite',

    constructor: function(config) {
        Ext.apply(config, {
            type: 'circle'
        });
        this.callParent([config]);
    }

});

Ext.define('TSP.view.ann.drawing.NeuronWeightSprite', {
    extend: 'Ext.draw.Sprite',
    alias: 'widget.neuronweightsprite',

    style: {
        'stroke-width': "1",
        stroke: 'black'
    },

    constructor: function(config) {
        Ext.apply(config, this.style, {
            type: "path",
            fromNeuron: config.fromNeuron,
            toNeuron: config.toNeuron,
            weightText: config.weightText,
            listeners: {
                mouseover: function(weight, e) {
                    this.toggleHighlight(true);
                },
                mouseout: function(weight, e) {
                    this.toggleHighlight(false);
                }
            }
        });
        this.callParent([config]);
    },

    toggleHighlight: function(over) {
        if( over ) {
            this.setStyle({'stroke-width': '3', stroke: 'blue'});
            this.weightText.setStyle({fill: 'red', stroke: 'black', 'font-weight': 'bold', 'font-size': '22px'});
        }
        else {
            this.setStyle({'stroke-width': '1', stroke: 'black'});
            this.weightText.setStyle({fill: 'black', stroke: 'none', 'font-weight': 'normal', 'font-size': '10px'});
        }
    },

    /**
     * @overwrite
     */
    redraw: function() {
        this.setAttributes({
            path: this.createWeight(this.fromNeuron, this.toNeuron)
        });
        this.callParent([this]);
    },

    createWeight: function(fromNeuron, toNeuron) {
        return 'M ' + fromNeuron.x + ',' + fromNeuron.y + 'L' + toNeuron.x + ',' + toNeuron.y;
    }

});

Ext.define('TSP.view.ann.drawing.NeuronWeightTextSprite', {
    extend: 'Ext.draw.Sprite',
    alias: 'widget.neuronweighttextsprite',

    constructor: function(config) {
        Ext.apply(config, {
            type: 'text',
            fill: '#000',
            font: '10px Arial'
        });
        this.callParent([config]);
    }

});

Ext.define('TSP.view.ann.drawing.NeuronActivationTextSprite', {
    extend: 'Ext.draw.Sprite',
    alias: 'widget.neuronactivationtextsprite',

    constructor: function(config) {
        Ext.apply(config, {
            type: 'text',
            fill: '#060161',
            font: '10px Arial'
        });
        this.callParent([config]);
    }

});