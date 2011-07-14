Ext.define('TSP.model.TrainingSet', {
    extend: 'Ext.data.Model',
    fields: ['id', 'name', 'numInputs', 'numOutputs'],
    hasMany: {
        model: 'TSP.model.TrainingSetIO',
        name: 'trainingSetIOs'
    }
});
