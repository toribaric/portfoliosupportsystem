Ext.define('TSP.model.Neuron', {
    extend: 'Ext.data.Model',
    fields: ['id', 'numInputs', 'activation', 'error'],
    hasMany: {
        model: 'TSP.model.NeuronWeight',
        name: 'neuronWeights'
    }
});
