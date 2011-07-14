Ext.define('TSP.controller.ann.Manager', {
    extend: 'Ext.app.Controller',

    stores: ['NeuralNetworks'],
    models: ['NeuralNetwork', 'NeuronLayer', 'Neuron', 'NeuronWeight'],
    views: ['ann.Manager', 'ann.List', 'ann.ListDetails', 'ann.Create'],

    refs: [
        {
            ref: 'annList',
            selector: 'annlist'
        },
        {
            ref: 'annListDetails',
            selector: 'annlistdetails'
        }
    ],

    init: function() {
        this.control({
            'annmanager annlist': {
                selectionchange: this.listSelectionChanged
            },
            'annmanager button[action=createNew]': {
                click: this.createNewAnn
            },
            'anncreate button[action=saveNewAnn]': {
                click: this.saveNewAnn
            }
        });
    },

    /**
     * loads details of network selected on grid
     */
    listSelectionChanged: function(sm, rs) {
        if( rs.length ) {
            var selectedId = rs[0].data.id;

            var store = this.getStore('NeuralNetworks');
            var annModel = store.getById(selectedId);

            var annListDetails = this.getAnnListDetails();
            annListDetails.showAnnDetails(annModel);
        }
    },

    /**
     * shows new neural network window
     */
    createNewAnn: function(button) {
        var win = Ext.create('TSP.view.ann.Create').show();
    },

    /**
     * saves new neural network
     */
    saveNewAnn: function(button) {
        var annList = this.getAnnList();

        var win = button.up('window');
        var form = win.down('form').getForm();

        if( form.isValid() ){
            form.submit({
                method: 'POST',
                url: 'ann/saveNew',
                success: function(form, action) {

                    if( action.result.success ) {
                        Ext.MessageBox.alert({
                            title: 'Success',
                            msg: 'Network created successfully!',
                            buttons: Ext.MessageBox.OK,
                            fn: function(buttonId) {
                                switch(buttonId) {
                                    case 'ok':
                                        win.close();
                                    break;
                                }
                            },
                            scope: this
                        });
                        // refresh datagrid and select inserted (last) row
                        annList.store.load(function() {
                            annList.getSelectionModel().select(annList.store.getTotalCount()-1);
                        });
                    }
                    else {
                        Ext.MessageBox.alert('Failed', 'Failed creating network: ' + action.result.errorMsg);
                    }

                },
                failure: function(form, action) {
                    Ext.MessageBox.alert('Failed', 'Failed creating network: ' + action.result.errorMsg);
                }
            });
        }

    }

});