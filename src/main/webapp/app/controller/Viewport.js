Ext.define('TSP.controller.Viewport', {
    extend: 'Ext.app.Controller',

    views: ['MainMenu', 'Viewport'],

    refs: [
        {
            ref: 'viewPort',
            selector: 'viewport'
        }
    ],

    init: function() {
        this.control({
            'mainmenu menu': {
                click: this.itemClicked
            }
        });
    },

    itemClicked: function(menu, item) {

        var viewport = this.getViewPort();

        var center = viewport.getComponent(2);

        switch(item.action) {
            case 'manageNetworks':
                center.removeAll();
                center.add(
                    {
                        xtype: 'annmanager',
                        id: 'annmanager'
                    }
                );
            break;
            case 'trainingSets':
                center.removeAll();
                center.add(
                    {
                        xtype: 'anntrainingsets',
                        id: 'anntrainingsets'
                    }
                );
            break;
        }

        center.doLayout();

    }


});