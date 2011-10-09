Ext.define('TSP.model.NeuralNetwork', {
    extend: 'Ext.data.Model',
    fields: ['id', 'name', 'description', 'numInputNeurons', 'numOutputNeurons', 'numHiddenLayers', 'activationFunction', 'validationError', 'r2', 'errorEnergy', 'trained'],
    hasMany: {
        model: 'TSP.model.NeuronLayer',
        name: 'neuronLayers'
    }
});
