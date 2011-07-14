Ext.define('TSP.controller.ann.Options', {
    extend: 'Ext.app.Controller',

    stores: ['NeuralNetworks'],
    models: ['NeuralNetwork', 'NeuronLayer', 'Neuron', 'NeuronWeight'],
    views: ['ann.List', 'ann.ListDetails', 'ann.details.ModifyStructure', 'ann.details.Options'],

    refs: [
        {
            ref: 'annList',
            selector: 'annlist'
        },
        {
            ref: 'annListDetails',
            selector: 'annlistdetails'
        },
        {
            ref: 'annModifyStructure',
            selector: 'annmodifystructure'
        },
        {
            ref: 'annOptions',
            selector: 'annoptions'
        }
    ],

    init: function() {
        this.control({
            'annoptions button[action=modifyStructure]': {
                click: this.openModifyStructureWindow
            },
            'annoptions button[action=deleteNetwork]': {
                click: this.deleteNetwork
            },
            'annoptions button[action=updateAnnConf]': {
                click: this.updateConfiguration
            },
            'annmodifystructure button[action=updateStructure]': {
                click: this.updateStructure
            }
        });
    },

    openModifyStructureWindow: function() {
        var networkId = this.getSelectedAnnListRow().data.id;

        var store = this.getStore('NeuralNetworks');
        var annModel = store.getById(networkId);

        var win = Ext.create('TSP.view.ann.details.ModifyStructure');
        win.showWindow(annModel);
    },

    deleteNetwork: function() {
        var annList = this.getAnnList();
        var annListDetails = this.getAnnListDetails();

        var selectedAnnRow = this.getSelectedAnnListRow();

        Ext.MessageBox.alert({
            title: 'Confirmation',
            msg: 'Are you sure?',
            icon: Ext.MessageBox.QUESTION,
            buttons: Ext.MessageBox.YESNO,
            fn: function(buttonId) {
                switch(buttonId) {
                    case 'yes':
                        annList.store.remove(selectedAnnRow);
                        annList.store.sync();
                        annListDetails.hideAnnDetails();
                    break;
                }
            },
            scope: this
        });

    },

    updateStructure: function(button) {
        var me = this;

        var networkId = this.getSelectedAnnListRow().data.id;

        var win = this.getAnnModifyStructure();
        var structure = win.getModifiedStructure();

        Ext.Ajax.request({
            url: 'ann/updateStructure',
            params: {
                id: networkId,
                structure: structure
            },
            success: function ( result, request ) {
                var response = JSON.parse(result.responseText);
                if( response.success == true ) {
                    Ext.MessageBox.alert({
                        title: 'Success',
                        msg: 'Network structure updated successfully!',
                        icon: Ext.MessageBox.INFO,
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

                    // refresh datagrid and select reselect row which was selected before update
                    me.refreshAnnListAfterUpdate();

                }
                else {
                    Ext.MessageBox.alert('Failed', 'Failed updating network structure: ' + response.errorMsg);
                }
            },
            failure: function ( result, request) {
                Ext.MessageBox.alert('Failed', 'Failed, response data: ' + result.responseText);
            }
        });
    },

    updateConfiguration: function(button) {
        var me = this;
        var form = this.getAnnOptions().items.get(1).down('form').getForm();
        if( form.isValid() ){
            form.submit({
                method: 'POST',
                url: 'ann/updateConfiguration',
                success: function(form, action) {

                     // refresh datagrid and select reselect row which was selected before update
                    me.refreshAnnListAfterUpdate();

                    Ext.MessageBox.alert({title:'Success', msg:'Configuration updated successfully!', icon: Ext.MessageBox.INFO, buttons:Ext.MessageBox.OK});
                },
                failure: function(form, action) {
                    Ext.MessageBox.alert({title:'Failed', msg:'Failed processing data: ' + action.result.error, icon: Ext.MessageBox.ERROR, buttons:Ext.MessageBox.OK});
                }
            });
        }
    },

    refreshAnnListAfterUpdate: function() {
        var annList = this.getAnnList();
        var selectedRow = annList.store.indexOf(this.getSelectedAnnListRow());
        annList.store.load(function() {
            annList.getSelectionModel().select(selectedRow);
        });
    },

    getSelectedAnnListRow: function() {
        var annList = this.getAnnList();
        var selected = annList.getSelectionModel().getSelection();
        return selected[0];
    }

});