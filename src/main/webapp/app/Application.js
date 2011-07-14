Ext.onReady(function(){
    Ext.Loader.setConfig({enabled:true});
});

Ext.application({
    name: 'TSP',

    controllers: [
        'Viewport',
        'ann.Manager',
        'ann.Options',
        'ann.TrainingSets',
        'ann.Training'
    ]
});
