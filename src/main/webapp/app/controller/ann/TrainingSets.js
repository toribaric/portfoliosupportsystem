Ext.define('TSP.controller.ann.TrainingSets', {
    extend: 'Ext.app.Controller',

    stores: ['TrainingSets'],
    models: ['TrainingSet'],
    views: ['ann.trainingsets.TrainingSets', 'ann.trainingsets.List', 'ann.trainingsets.Create', 'ann.trainingsets.TimeSeries'],

    refs: [
        {
            ref: 'tsList',
            selector: 'tslist'
        },
        {
            ref: 'tsCreate',
            selector: 'tscreate'
        },
        {
            ref: 'annTrainingSets',
            selector: 'anntrainingsets'
        },
        {
            ref: 'tsTimeSeries',
            selector: 'tstimeseries'
        }
    ],

    init: function() {
        this.control({
            'tscreate button[action=createTrainingSet]': {
                click: this.createTrainingSet
            },
            'tscreate button[action=resetTrainingSet]': {
                click: this.resetTrainingSetFields
            },
            'tscreate button[action=uploadCreateTrainingSet]': {
                click: this.uploadCreateTrainingSet
            },
            'tscreate button[action=uploadResetTrainingSetFields]': {
                click: this.uploadResetTrainingSetFields
            },
            'tslist button[action=deleteTrainingSet]': {
                click: this.deleteTrainingSet
            },
            'tstimeseries button[action=uploadTimeSeriesData]': {
                click: this.createTimeSeriesChart
            },
            'tstimeseries button[action=resetTimeSeriesForm]': {
                click: this.resetTimeSeriesForm
            },
            'tstimeseries button[action=generateTsRecords]': {
                click: this.generateTrainingSetRecordsFromChartData
            },
            'tslist': {
                selectionchange: this.createTrainingSetIoGrid
            }
        });
    },

    createTrainingSet: function(button) {
        var trainingSetList = this.getTsList();
        var fields = this.getCreateSetFields();

        // get field values
        var name = fields.setName.getValue();
        var numInputs = parseInt(fields.numInputs.getValue());
        var numOutputs = parseInt(fields.numOutputs.getValue());

        if( isNaN(numInputs) || isNaN(numOutputs) || numInputs <= 0 || numOutputs <= 0 ) {
            Ext.MessageBox.alert({title:'Failed', msg:'There must be at least one input and one output.', icon: Ext.MessageBox.ERROR, buttons:Ext.MessageBox.OK});
            return;
        }

        var newRecord = new TSP.model.TrainingSet({
            id: parseInt(0),
            name: name,
            numInputs: numInputs,
            numOutputs: numOutputs
        });

        // "write" listener which selects currently added training set - "write" fires after
        // proxy finishes writing to it's storage - which is also the end of sync store operation,
        // as sync calls proxy batch which fires "write" event when finished
        if( !trainingSetList.store.hasListener('write') ) {
            trainingSetList.store.on('write', function(store, operation) {
                store.load(function() {
                    if( operation.action == "create" ) {
                        trainingSetList.getSelectionModel().select(store.getTotalCount() - 1);
                    }
                });
            });
        }

        trainingSetList.store.add(newRecord);

        this.resetTrainingSetFields(null);

    },

    resetTrainingSetFields: function(button) {
        var fields = this.getCreateSetFields();
        fields.setName.setValue('');
        fields.numInputs.setValue('');
        fields.numOutputs.setValue('');
    },

    getCreateSetFields: function() {
        var tsCreate = this.getTsCreate();
        var fieldSet = tsCreate.down('fieldset');
        var fields = {
            setName: fieldSet.items.get('setName'),
            numInputs: fieldSet.items.get('numInputs'),
            numOutputs: fieldSet.items.get('numOutputs')
        };
        return fields;
    },

    uploadCreateTrainingSet: function(button) {
        var me = this;

        var trainingSetList = this.getTsList();

        var tsCreate = this.getTsCreate();
        var form = tsCreate.items.get('uploadForm').getForm();

        if(form.isValid()){
            form.submit({
                method: 'POST',
                url: 'ts/upload',
                waitMsg: 'Processing data...',
                success: function(form, action) {
                    me.uploadResetTrainingSetFields();

                    trainingSetList.store.load(function() {
                        trainingSetList.getSelectionModel().select(trainingSetList.store.getTotalCount() - 1);
                    });

                    Ext.MessageBox.alert({title:'Success', msg:'Training set created!', icon: Ext.MessageBox.INFO, buttons:Ext.MessageBox.OK});
                },
                failure: function(form, action) {
                    Ext.MessageBox.alert({title:'Failed', msg:'Failed creating training set: ' + action.result.error, icon: Ext.MessageBox.ERROR, buttons:Ext.MessageBox.OK});
                }
            });
        }

    },

    uploadResetTrainingSetFields: function() {
        var tsCreate = this.getTsCreate();
        var form = tsCreate.items.get('uploadForm').getForm();
        form.reset();
    },

    deleteTrainingSet: function(button) {
        var trainingSetList = this.getTsList();
        var tsGridSelectedRow = this.getTrainingSetGridSelectedRow();
        if( tsGridSelectedRow ) {
            Ext.MessageBox.alert({
                title: 'Confirmation',
                msg: 'Are you sure?',
                icon: Ext.MessageBox.QUESTION,
                buttons: Ext.MessageBox.YESNO,
                fn: function(buttonId) {
                    switch(buttonId) {
                        case 'yes':
                            trainingSetList.store.remove(tsGridSelectedRow);
                            // removing of training set record deselects it, so we're removing trainingSetIo grid panel and restoring default html
                            this.clearTrainingSetIoGridParent('<div id="trainingsets-initial-msg">Select training set to edit from "Training sets" grid</div>', function(trainingSetIoGridParent) {
                                trainingSetIoGridParent.doLayout();
                            });
                        break;
                    }
                },
                scope: this
            });
        }
    },

    getTrainingSetGridSelectedRow: function() {
        var trainingSetList = this.getTsList();
        return trainingSetList.getSelectionModel().getSelection()[0];
    },

    getTrainingSetIoGridParent: function() {
        var annTrainingSets = this.getAnnTrainingSets();
        return annTrainingSets.items.get(0).items.get(1);
    },

    clearTrainingSetIoGridParent: function(html, callback) {
        var trainingSetIoGridParent = this.getTrainingSetIoGridParent();
        trainingSetIoGridParent.removeAll(true);
        trainingSetIoGridParent.update(html);
        callback(trainingSetIoGridParent);
    },

    getTrainingSetModelById: function(trainingSetId) {
        var trainingSetStore = this.getStore('TrainingSets');
        return trainingSetStore.getById(trainingSetId);
    },

    /*
     *  TrainingSetIo - or training set details - grid panel creation functions
     */
    createTrainingSetIoGrid: function(sm, rs) {

        if( rs.length <= 0 ) {
            return;
        }

        // selected training set id and name
        var trainingSetId = rs[0].data.id;
        var trainingSetName = rs[0].data.name;

        // get selected training set model
        var trainingSetModel = this.getTrainingSetModelById(trainingSetId);

        // create trainingSetIo model, store and columns for grid panel based on
        // number of fields of trainingSet (inputs and outputs)
        var trainingSetIoStore = this.createTrainingSetIoStore(trainingSetModel);
        var columns = this.createTrainingSetIoGridColumns(trainingSetModel);

        var rowEditingPlugin = Ext.create('Ext.grid.plugin.RowEditing', {
            clicksToMoveEditor: 1,
            autoCancel: false
        });

        // create trainingSetIos grid panel
        var trainingSetIoGrid = new Ext.grid.Panel ({
            title: 'Edit training set "' + trainingSetName + '"',
            border: false,
            height: 300,
            store: trainingSetIoStore,
            columns: columns,
            tbar: [
                {
                    text: 'Add',
                    iconCls: 'icon-add',
                    handler : function() {
                        rowEditingPlugin.cancelEdit();
                        // create new trainingSetIo model instance (record) with empty data; user will
                        // fill data and update row - only then sync will be performed, not now
                        var newRecord = Ext.ModelManager.create([trainingSetId], 'TrainingSetIO' + trainingSetId);
                        trainingSetIoStore.insert(0, newRecord);
                        rowEditingPlugin.startEdit(0, 0);
                    }
                },
                {
                    itemId: 'removeIO',
                    text: 'Remove',
                    iconCls: 'icon-delete',
                    handler: function() {
                        /*
                         * TODO: fix bug: last record removal from store isn't getting dirty thus it's not syncing
                         */
                        rowEditingPlugin.cancelEdit();
                        // here we must manually mark data as changed (or make it dirty) because as editList
                        // grid's store isn't loaded after record insertion, removal wouldn't make any change
                        // to records from store's point of view, and sync wouldn't synchronize anything
                        var tsGridSelectedRow = trainingSetIoGrid.getSelectionModel().getSelection()[0];
                        tsGridSelectedRow.data.dirty = true;
                        trainingSetIoStore.remove(tsGridSelectedRow);
                        trainingSetIoStore.sync();
                    },
                    disabled: true
                }
            ],
            plugins: [rowEditingPlugin],
            listeners: {
                'selectionchange': function(view, records) {
                    this.down('#removeIO').setDisabled(!records.length);
                },
                // fired when user click 'update' button of RowEditing plugin
                'edit': function(editor, e) {
                    trainingSetIoStore.sync();
                }
            }
        });

        // finally clear trainingSetIo grid container (parent) from old elements
        // and add newly created grid panel to it
        this.clearTrainingSetIoGridParent('', function(trainingSetIoGridParent) {
            trainingSetIoGridParent.items.add(trainingSetIoGrid);
            trainingSetIoGridParent.doLayout();
        });

    },

    createTrainingSetIoModel: function(trainingSetModel) {

        var fields = ['trainingSetId'];
        for( var i = 0; i < trainingSetModel.get('numInputs'); i++ ) {
            var fieldName = 'input' + (i+1);
            fields.push(fieldName);
        }
        for( var i = 0; i < trainingSetModel.get('numOutputs'); i++ ) {
            var fieldName = 'output' + (i+1);
            fields.push(fieldName);
        }

        var modelName = 'TrainingSetIO' + trainingSetModel.get('id');
        if( Ext.ModelManager.getModel(modelName) == undefined ) {
            Ext.define(modelName, {
                extend: 'Ext.data.Model',
                fields: fields
            });
        }

        return modelName;
    },

    createTrainingSetIoStore: function(trainingSetModel) {

        var modelName = this.createTrainingSetIoModel(trainingSetModel);

        var store = new Ext.data.Store( {
            autoLoad: true,
            autoSync: false,
            model: modelName,
            proxy: {
                type: 'ajax',
                api: {
                    read: 'ts/io/list?trainingSetId=' + trainingSetModel.get('id'),
                    create: 'ts/io/update',
                    update: 'ts/io/update'
                },
                writer: {
                    type: 'json',
                    writeAllFields: false
                },
                reader: {
                    type: 'json',
                    root: 'trainingSetIOs'
                },
                listeners: {
                    exception: function(proxy, response, operation){
                        Ext.MessageBox.show({
                            title: 'Failed',
                            msg: response,
                            icon: Ext.MessageBox.ERROR,
                            buttons: Ext.Msg.OK
                        });
                    }
                }
            }
        });

        return store;
    },

    createTrainingSetIoGridColumns: function(trainingSetModel) {
        var columns = new Array();
        for( var i = 0; i < trainingSetModel.get('numInputs'); i++ ) {
            columns.push({header: 'Input '+(i+1), dataIndex: 'input'+(i+1), flex: 1, editor: { allowBlank: true } });
        }
        for( var i = 0; i < trainingSetModel.get('numOutputs'); i++ ) {
            columns.push({header: 'Output '+(i+1), dataIndex: 'output'+(i+1), flex: 1, editor: { allowBlank: true } });
        }
        return columns;
    },
    /*
     *  /TrainingSetIo - or training set details - grid panel creation functions
     */

    /*
     *  Time series upload / training set record creation functions
     */
    createTimeSeriesChart: function(button) {

        var me = this;
        var form = this.getTimeSeriesForm();

        if( form.isValid() ){
            form.submit({
                method: 'POST',
                url: 'ts/uploadTimeSeries',
                waitMsg: 'Processing data...',
                success: function(form, action) {

                    // encoded JSON object containing uploaded time series data (dates and values)
                    var timeSeries = action.result.timeSeries;

                    // assemble data form chart's JsonStore, create store and add data to it
                    var data = new Array();
                    for( var i = 0; i < timeSeries.length; i++ ) {
                        data.push({date: timeSeries[i].date, data: timeSeries[i].data});
                    }

                    var store = Ext.create('Ext.data.JsonStore', {
                        fields: ['date', 'data'],
                        data: data
                    });

                    // assemble and show chart with time series data
                    var chart = me.assembleChart(me, store);
                    me.addChartToContainer(chart);

                    // show training set records generator form
                    me.toggleTimeSeriesTsRecGeneratorForm();

                },
                failure: function(form, action) {
                    Ext.MessageBox.alert({title:'Failed', msg:'Failed processing data: ' + action.result.error, icon: Ext.MessageBox.ERROR, buttons:Ext.MessageBox.OK});
                }
            });
        }

    },

    generateTrainingSetRecordsFromChartData: function() {

        // get id of selected training set row - if it's false, no training set has been selected
        var trainingSetId = this.getSelectedTrainingSetId();
        if( trainingSetId == false ) {
            return;
        }

        // get form, check if field values are valid and get those values
        var form = this.getTimesSeriesTsRecGeneratorForm();
        if( !form.isValid() ) {
            return;
        }
        var numRecords = form.findField('numTsRecords').getValue();
        var frequency = form.findField('samplingFrequency').getValue();

        // get chart's store which contains time series data
        var chartContainer = this.getChartContainer();
        var chart = chartContainer.items.get(0);
        var chartStore = chart.store;

        // add time series data to "timeSeries" array
        var timeSeries = new Array();
        for( var i = 0; i < chartStore.getCount(); i++ ) {
            var model = chartStore.getAt(i);
            timeSeries.push(model.get('data'));
        }

        // send time series data and other params to server which will generate selected training set
        // records from them
        var me = this;
        Ext.Ajax.request({
            url: 'ts/generateTrainingSetFromTimeSeries',
            params: {
                trainingSetId: trainingSetId,
                timeSeries: timeSeries,
                numRecords: numRecords,
                frequency: frequency
            },
            success: function ( result, request ) {
                var response = JSON.parse(result.responseText);
                if( response.success == true ) {
                    // reload training set IOs store if successful
                    var trainingSetIoStore = me.getSelectedTrainingSetIoStore();
                    trainingSetIoStore.load();
                    Ext.MessageBox.alert({title:'Done', msg:'Training set records generated successfully!', icon: Ext.MessageBox.INFO, buttons:Ext.MessageBox.OK});
                }
                else {
                    Ext.MessageBox.alert({title:'Failed', msg: 'Failed generating training set records: ' + response.errorMsg, icon: Ext.MessageBox.ERROR, buttons:Ext.MessageBox.OK});
                }
            },
            failure: function ( result, request) {
                Ext.MessageBox.alert('Failed', 'Failed, response data: ' + result.responseText);
            }
        });

    },

    addChartToContainer: function(chart) {
        var chartContainer = this.getChartContainer();
        chartContainer.removeAll(true);
        chartContainer.items.add(chart);
        chartContainer.doLayout();
    },

    assembleChart: function(me, store) {

        var firstItemSelected = null;

        var chart = Ext.create('Ext.chart.Chart', {
            animate: true,
            store: store,
            axes: [
                {
                    type: 'Category',
                    position: 'bottom',
                    fields: ['date'],
                    label: {
                        rotate: {
                            degrees: 315
                        },
                        font: '10px Arial'
                    },
                    title: 'Time',
                    grid: true
                },
                {
                    type: 'Numeric',
                    position: 'left',
                    fields: ['data'],
                    title: 'Values',
                    grid: true,
                    label: {
                        font: '10px Arial'
                    }
                }
            ],
            series: [
                {
                    type: 'line',
                    highlight: {
                        size: 4,
                        radius: 5
                    },
                    axis: 'left',
                    fill: true,
                    xField: 'date',
                    yField: 'data',
                    listeners: {
                        itemmouseup: function(item) {
                            me.selectChartData(me, item, store);
                        }
                    },
                    tips: {
                        trackMouse: false,
                        renderer: function(storeItem, item) {
                            this.update('<div class="chart-tip"><span>' + storeItem.get('date') + ':</span> ' + storeItem.get('data') + '</div>');
                        }
                    },
                    markerCfg: {
                        type: 'circle',
                        size: 4,
                        radius: 4,
                        'stroke-width': 0
                    }
                }
            ]
        });

        return chart;
    },

    selectChartData: function(me, selectedChartItem, chartStore) {

        // add to "chartData" data (from chart's store) from first selected date (start) to last selected (end)
        var chartData = new Array();

        // get id of selected training set row - if it's false, no training set has been selected
        var trainingSetId = me.getSelectedTrainingSetId();
        if( trainingSetId == false ) {
            return false;
        }

        // get selected training set's number of inputs and outputs to add their sum (total number of fields) to
        // value of chart selection start, thus forming selection of chart data to add as new training record to selected training set
        var trainingSetModel = me.getTrainingSetModelById(trainingSetId);
        var numIoFields = trainingSetModel.get('numInputs') + trainingSetModel.get('numOutputs');

        // define start and end points of chart data selection; if end point is beyond chart's end (or chart's store end), return and display error
        var start = selectedChartItem.value[0];
        var end = start + numIoFields - 1;
        if( end >= chartStore.getCount() ) {
            Ext.MessageBox.alert({title:'Error', msg:'End point of selected training record is not contained on time series chart.' , icon: Ext.MessageBox.ERROR, buttons:Ext.MessageBox.OK});
            return false;
        }

        // add selected data to chartData array
        for( var i = start; i <= end; i++ ) {
            var model = chartStore.getAt(i);
            chartData.push(model.get('data'));
        }

        // add collected data from chart to selected training set
        if( me.addChartDataToTrainingSet(me, chartData, trainingSetId) ) {
            Ext.MessageBox.alert({title:'Done', msg:'Training set record added to selected training set!', icon: Ext.MessageBox.INFO, buttons:Ext.MessageBox.OK});
        }

    },

    addChartDataToTrainingSet: function(me, chartData, trainingSetId) {

        // get training set i/o grid and it's store to which we'll add new data from chart
        var trainingSetIoStore = me.getSelectedTrainingSetIoStore();

        // create new TrainingSetIO record from chart data and add it to TrainingSetIO store
        var newData = [trainingSetId].concat(chartData);
        var newRecord = Ext.ModelManager.create(newData, 'TrainingSetIO' + trainingSetId);

        trainingSetIoStore.add(newRecord);
        trainingSetIoStore.sync();

        return true;
    },

    getSelectedTrainingSetId: function() {
        // get selected row from training set grid; if none is selected, return
        var tsGridSelectedRow = this.getTrainingSetGridSelectedRow();
        if( !tsGridSelectedRow ) {
            Ext.MessageBox.alert({title:'Error', msg:'Please select a training set from "Training sets" grid.', icon: Ext.MessageBox.ERROR, buttons:Ext.MessageBox.OK});
            return false;
        }
        // return id of selected training set row
        return tsGridSelectedRow.data.id;
    },

    getSelectedTrainingSetIoStore: function() {
        var trainingSetIoGridParent = this.getTrainingSetIoGridParent();
        var trainingSetIoGrid = trainingSetIoGridParent.items.get(0);
        return trainingSetIoGrid.store;
    },

    getChartContainer: function() {
        var tsTimeSeries = this.getTsTimeSeries();
        return tsTimeSeries.items.get('chartContainer');
    },

    getTimeSeriesFormsVbox: function() {
        var tsTimeSeries = this.getTsTimeSeries();
        return tsTimeSeries.items.get('timeSeriesForms');
    },

    getTimeSeriesForm: function() {
        var formsVbox = this.getTimeSeriesFormsVbox();
        return formsVbox.items.get('createSetForm').getForm();
    },

    resetTimeSeriesForm: function() {
        this.getTimeSeriesForm().reset();
    },

    getTimesSeriesTsRecGeneratorForm: function() {
        var formsVbox = this.getTimeSeriesFormsVbox();
        return formsVbox.items.get('generateSetRecordsForm').getForm();
    },

    toggleTimeSeriesTsRecGeneratorForm: function() {
        var formsVbox = this.getTimeSeriesFormsVbox();
        var form = formsVbox.items.get('generateSetRecordsForm');
        if( !form.isVisible() ) {
            form.show();
        }
    }
    /*
     *  /Time series upload / training set record creation functions
     */


});