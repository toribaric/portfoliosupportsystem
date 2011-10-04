Ext.define('TSP.controller.ann.Training', {
    extend: 'Ext.app.Controller',

    requires: [
        'Ext.direct.*'
    ],

    stores: ['TrainingSets', 'NeuralNetworks'],
    models: ['TrainingSet', 'NeuralNetwork'],
    views: ['ann.List', 'ann.details.Options', 'ann.training.Setup', 'ann.training.Data', 'ann.training.SimpleData', 'ann.training.Test', 'ann.ListDetails'],

    refs: [
        {
            ref: 'annList',
            selector: 'annlist'
        },
        {
            ref: 'annOptions',
            selector: 'annoptions'
        },
        {
            ref: 'trainingSetup',
            selector: 'trainingsetup'
        },
        {
            ref: 'trainingData',
            selector: 'trainingdata'
        },
        {
            ref: 'simpleTrainingData',
            selector: 'simpletrainingdata'
        },
        {
            ref: 'testNetwork',
            selector: 'testnetwork'
        },
        {
            ref: 'annListDetails',
            selector: 'annlistdetails'
        }
    ],

    init: function() {
        this.control({
            'annoptions button[action=train]': {
                click: this.showTrainingSetupWindow
            },
            'trainingsetup button[action=startTraining]': {
                click: this.startTraining
            },
            'trainingdata button[action=stopTraining]': {
                click: this.stopTraining
            },
            'simpletrainingdata button[action=stopTraining]': {
                click: this.stopTraining
            },
            'annoptions button[action=test]': {
                click: this.openTestWindow
            },
            'testnetwork button[action=testNetwork]': {
                click: this.testNetwork
            }
        });
    },

    showTrainingSetupWindow: function(button) {

        var neuralNetworkId = this.getSelectedAnnListRow().data.id;
        var neuralNetworkTrained = this.getSelectedAnnListRow().data.trained;

        var trainingSetsStore = this.getStore('TrainingSets');

        var trainingDataWin = this.returnRegisteredTrainingDataWindow(neuralNetworkId);

        if( neuralNetworkTrained && trainingDataWin == null ) {
            Ext.MessageBox.alert({
                title: 'Confirmation',
                msg: 'This network is already trained, new training will reset its weights. Are you sure?',
                icon: Ext.MessageBox.WARNING,
                buttons: Ext.MessageBox.YESNO,
                fn: function(buttonId) {
                    switch(buttonId) {
                        case 'yes':
                            this.createTrainingSetupWindow(neuralNetworkId, trainingSetsStore);
                        break;
                    }
                },
                scope: this
            });
        }
        else {
            // if this network's training window is registered, that means that network training is
            // in process, so show that win instead of training setup win
            if( trainingDataWin != null ) {
                trainingDataWin.show();
                return;
            }
            this.createTrainingSetupWindow(neuralNetworkId, trainingSetsStore);
        }

    },

    createTrainingSetupWindow: function(neuralNetworkId, trainingSetsStore) {
        var trainingSetupWin = Ext.create('TSP.view.ann.training.Setup', Ext.apply({neuralNetworkId: neuralNetworkId, trainingSetStore: trainingSetsStore}));
        trainingSetupWin.show();
    },

    startTraining: function(button) {
        var me = this;

        var trainingSetupWin = this.getTrainingSetup();

        // is this checkbox is checked, we're showing simple training window without chart
        var showChart = trainingSetupWin.down('fieldset').items.get('showChart').getValue();

        var neuralNetworkId = this.getSelectedAnnListRow().data.id;
        var neuralNetworkName = this.getSelectedAnnListRow().data.name;

        var form = button.up('trainingsetup').down("form").getForm();

        if( form.isValid() ) {
            form.submit({
                method: 'POST',
                url: 'ann/train',
                success: function(form, action) {
                    // close training setup window
                    trainingSetupWin.close();

                    // reload networks store and grid after weights and trained state reset
                    me.reloadAnnListStore();

                    var trainingDataWin = me.createTrainingDataWindow(showChart, neuralNetworkId, neuralNetworkName);

                    // initiate polling
                    me.initiateTrainingDataPoll(neuralNetworkId, trainingDataWin, me);
                },
                failure: function(form, action) {
                    Ext.MessageBox.alert({title:'Failed', msg:'Failed starting training: ' + action.result.errorMsg, icon: Ext.MessageBox.ERROR, buttons:Ext.MessageBox.OK});
                }
            });
        }

    },

    createTrainingDataWindow: function(showChart, neuralNetworkId, neuralNetworkName) {
        var trainingDataWin = null;
        if( showChart ) {
            trainingDataWin = this.createChartTrainingDataWindow(neuralNetworkId, neuralNetworkName);
        }
        else {
            trainingDataWin = this.createSimpleTrainingDataWindow(neuralNetworkId, neuralNetworkName);
        }
        return trainingDataWin;
    },

    createChartTrainingDataWindow: function(neuralNetworkId, neuralNetworkName) {
        var trainingDataWinId = 'trainingData' + neuralNetworkId;
        var trainingDataWin = Ext.create('TSP.view.ann.training.Data', Ext.apply({id: trainingDataWinId, neuralNetworkId: neuralNetworkId}));
        trainingDataWin.setTitle('Training data for network "' + neuralNetworkName + '"');
        trainingDataWin.setWidth(600);
        trainingDataWin.setHeight(500);
        trainingDataWin.show();
        trainingDataWin.center();
        return trainingDataWin;
    },

    createSimpleTrainingDataWindow: function(neuralNetworkId, neuralNetworkName) {
        var trainingDataWinId = 'trainingData' + neuralNetworkId;
        var trainingDataWin = Ext.create('TSP.view.ann.training.SimpleData', Ext.apply({id: trainingDataWinId, neuralNetworkId: neuralNetworkId}));
        trainingDataWin.setTitle('Training data for network "' + neuralNetworkName + '"');
        trainingDataWin.setWidth(280);
        trainingDataWin.setHeight(190);
        trainingDataWin.show();
        trainingDataWin.center();
        return trainingDataWin;
    },

    returnRegisteredTrainingDataWindow: function(neuralNetworkId) {
        var trainingDataWinId = 'trainingData' + neuralNetworkId;
        var trainingDataWin = Ext.getCmp(trainingDataWinId);
        if( trainingDataWin != undefined  ) {
            return trainingDataWin;
        }
        return null;
    },

    initiateTrainingDataPoll: function(neuralNetworkId, trainingDataWin, me) {

        var pollProviderId = 'trainingDataPolling' + neuralNetworkId;

        Ext.direct.Manager.addProvider({
            id: pollProviderId,
            baseParams: {
                neuralNetworkId: neuralNetworkId
            },
            type:'polling',
            url: 'ann/getTrainingData',
            interval: 1000,
            listeners: {
                data: function(provider, event) {
                    var trainingData = event.data;

                    me.fillTrainingDataWindowComponents(trainingDataWin, trainingData);

                    // if training is finished discard polling provider
                    if( !trainingData.trainingInProcess ) {
                        provider.disconnect();
                        Ext.direct.Manager.removeProvider(provider);
                        // set training data window close mode to close, not hide anymore
                        trainingDataWin.setCloseMode('close');
                        // update this network's trained status
                        me.updateTrainedStatus(neuralNetworkId, true, function() {
                            me.reloadAnnListStore();
                        });
                    }
                }
            }
        });

    },

    fillTrainingDataWindowComponents: function(trainingDataWin, trainingData) {
        // get components from data window to which polled data will be set
        var chart = trainingDataWin.down('chart');
        var displayFields = trainingDataWin.items.get(0).items.get(0);
        var iterationField = displayFields.items.get('iteration');
        var totalErrorField = displayFields.items.get('totalError');
        var validationErrorField = displayFields.items.get('validationError');
        var rSquaredField = displayFields.items.get('rSquared');

        // fill data to load to chart's store and fields
        var chartData = this.getChartData(trainingData);

        // set fields data (current iteration and error - or last ones in array)
        iterationField.setValue(chartData[chartData.length - 1].iteration);
        totalErrorField.setValue(chartData[chartData.length - 1].error.toFixed(8));
        validationErrorField.setValue(trainingData.validationError.toFixed(8));
        rSquaredField.setValue(trainingData.rSquared.toFixed(8));

        // if chart doesn't exist on window simple data window is displayed; don't feed data
        if( chart != undefined ) {
            // load data to chart's store
            if( chartData.length > 1 ) {
                chart.store.loadData(chartData);
            }
        }
    },

    getChartData: function(trainingData) {
        var chartData = new Array();
        for( var i = 0; i < trainingData.totalErrors.length; i++ ) {
            chartData.push({
                iteration: trainingData.trainingIterations[i],
                error: trainingData.totalErrors[i]
            });
        }
        return chartData;
    },

    stopTraining: function(button) {

        var stoppingWindow = button.up('window');
        var neuralNetworkId = stoppingWindow.getNeuralNetworkId();

        Ext.Ajax.request({
            url: 'ann/stopTraining',
            params: {
                neuralNetworkId: neuralNetworkId
            },
            success: function ( result, request ) {
                var response = JSON.parse(result.responseText);
                if( !response.success ) {
                    Ext.MessageBox.alert({title:'Failed', msg:'Failed stopping training: ' + response.errorMsg, icon: Ext.MessageBox.ERROR, buttons:Ext.MessageBox.OK});
                }
            },
            failure: function ( result, request) {
                Ext.MessageBox.alert({title:'Failed', msg:'Failed, response data: ' + result.responseText, icon: Ext.MessageBox.ERROR, buttons:Ext.MessageBox.OK});
            }
        });

    },

    updateTrainedStatus: function(neuralNetworkId, trained, updateAnnList) {
        Ext.Ajax.request({
            url: 'ann/updateConfiguration',
            params: {
                id: neuralNetworkId,
                trained: trained
            },
            success: function ( result, request ) {
                var response = JSON.parse(result.responseText);
                if( response.success ) {
                    updateAnnList();
                }
                else {
                    Ext.MessageBox.alert({title:'Failed', msg:'Failed updating trained status, error: ' + response.errorMsg, icon: Ext.MessageBox.ERROR, buttons:Ext.MessageBox.OK});
                }
            },
            failure: function ( result, request) {
                Ext.MessageBox.alert({title:'Failed', msg:'Failed updating trained status, response data: ' + result.responseText, icon: Ext.MessageBox.ERROR, buttons:Ext.MessageBox.OK});
            }
        });
    },

    openTestWindow: function(button) {
        var neuralNetworkId = this.getSelectedAnnListRow().data.id;

        var testWin = Ext.create('TSP.view.ann.training.Test', Ext.apply({neuralNetworkId: neuralNetworkId}));
        testWin.show();
    },

    testNetwork: function(button) {
        var me = this;
        var form = button.up('testnetwork').down("form").getForm();
        if( form.isValid() ) {
            form.submit({
                method: 'POST',
                url: 'ann/test',
                success: function(form, action) {
                    var outputs = action.result.outputs;
                    var displayOutputs = 'Calculated output(s): ';
                    for( var i = 0; i < outputs.length; i++ ) {
                        displayOutputs += outputs[i] + ' ';
                    }

                    // send inputs to network graph to show activation values of all neurons (including inputs)
                    var inputs = form.findField('networkInput').getValue();
                    me.feedInputsToNetworkGraph(inputs);

                    Ext.MessageBox.alert({title:'Calculated outputs', msg:displayOutputs.trim(), icon: Ext.MessageBox.INFO, buttons:Ext.MessageBox.OK});
                },
                failure: function(form, action) {
                    Ext.MessageBox.alert({title:'Failed', msg:'Failed calculating outputs: ' + action.result.errorMsg, icon: Ext.MessageBox.ERROR, buttons:Ext.MessageBox.OK});
                }
            });
        }
    },

    feedInputsToNetworkGraph: function(inputs) {
        var annListDetails = this.getAnnListDetails();
        var graphPanel = annListDetails.items.get('graphPanel');
        var annGraph = graphPanel.items.get('annGraph');
        annGraph.setNetworkInputs(inputs.split(/\s/));
        this.reloadAnnListStore();
    },

    getSelectedAnnListRow: function() {
        var annList = this.getAnnList();
        var selected = annList.getSelectionModel().getSelection();
        return selected[0];
    },

    reloadAnnListStore: function() {
        var annList = this.getAnnList();
        var selectedRow = annList.store.indexOf(this.getSelectedAnnListRow());
        annList.store.load(function() {
            annList.getSelectionModel().select(selectedRow);
        });
    }


});