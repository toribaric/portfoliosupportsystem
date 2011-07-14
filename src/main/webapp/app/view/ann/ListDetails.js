Ext.define('TSP.view.ann.ListDetails', {
    extend: 'Ext.Panel',
    alias: 'widget.annlistdetails',

    requires: [
        'TSP.view.ann.details.Options',
        'TSP.view.ann.drawing.AnnGraph'
    ],

    bodyPadding: 5,
    layout: 'border',
    border: true,

    initialMessage: '<div id="anndetailspanel-initial-msg">Select neural network from grid to view details.</div>',

    initComponent: function() {

        this.items = [
            {
                xtype: 'annoptions',
                itemId: 'annOptions',
                region: 'west',
                margin: '0 3 0 0',
                hidden: true
            },
            {
                xtype: 'panel',
                itemId: 'graphPanel',
                autoScroll: true,
                region: 'center',
                html: this.initialMessage,
                items: [
                    {
                        xtype: 'anngraph',
                        itemId: 'annGraph',
                        hidden: true
                    }
                ]
            }
        ];

        this.callParent(arguments);
    },

    showAnnDetails: function(annModel) {
        // show options panel, show annGraph widget and remove initial html message
        var optionsPanel = this.items.get('annOptions');
        this.setOptionsPanelConfigurationValues(optionsPanel, annModel);

        var graphPanel = this.items.get('graphPanel');
        var annGraph = graphPanel.items.get('annGraph');

        if( !optionsPanel.isVisible() ) {
            optionsPanel.show();
            annGraph.show();
            graphPanel.update('');
        }

        // draw neural network graph
        this.drawAnnGraph(annModel, graphPanel, annGraph);
    },

    drawAnnGraph: function(annModel, graphPanel, annGraph) {
        var annGraphWidth = graphPanel.getWidth() - 20;
        var annGraphHeight = annGraph.getNetworkGraphHeight(annModel)

        annGraph.setWidth(annGraphWidth);
        annGraph.setHeight(annGraphHeight);
        annGraph.drawGraph(annModel, graphPanel.getWidth());
    },

    hideAnnDetails: function() {
        // hide options panel, hide annGraph widget and display initial html message
        var optionsPanel = this.items.get('annOptions')
        var graphPanel = this.items.get('graphPanel');
        var annGraph = graphPanel.items.get('annGraph');
        if( optionsPanel.isVisible() ) {
            optionsPanel.hide();
            annGraph.hide();
            graphPanel.update(this.initialMessage);
        }
    },

    setOptionsPanelConfigurationValues: function(optionsPanel, annModel) {
        var configurationFields = this.getOptionsPanelConfigurationFields(optionsPanel);

        // set selected network id in hidden field
        var annId = annModel.get('id');
        configurationFields.annId.setValue(annId);

        // set transfer function in combobox
        var activationFunction = annModel.get('activationFunction');
        configurationFields.activationFunction.setValue(activationFunction);
    },

    getOptionsPanelConfigurationFields: function(optionsPanel) {
        var form = optionsPanel.items.get(1).down('form');
        var fieldSet = form.down('fieldset');
        var fields = {
            annId: fieldSet.items.get('annId'),
            activationFunction: fieldSet.items.get('activationFunction')
        }
        return fields;
    }

});

