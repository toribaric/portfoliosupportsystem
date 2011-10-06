Ext.define('TSP.model.NeuralNetwork', {
    extend: 'Ext.data.Model',
    fields: ['id', 'name', 'description', 'numInputNeurons', 'numOutputNeurons', 'numHiddenLayers', 'activationFunction', 'r2', 'trained'],
    hasMany: {
        model: 'TSP.model.NeuronLayer',
        name: 'neuronLayers'
    }
});
