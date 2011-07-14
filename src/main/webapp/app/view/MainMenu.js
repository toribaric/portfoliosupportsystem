Ext.define('TSP.view.MainMenu', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.mainmenu',

    requires: ['Ext.menu.Menu'],

	title: 'Options',
	collapsible: true,
	animCollapse: true,
    margin: '2 0 2 2',
    layout: 'fit',

    initComponent: function() {
        Ext.apply(this, {

			items: [{
				xtype: 'menu',
                plain: true,
                floating: false,
                border: false,
                items: [
                    {
                        text: 'Neural networks',
                        width: 174,
                        menu: [
                            {
                                text: 'Manage networks',
                                action: 'manageNetworks'
                            },
                            {
                                text: 'Training sets',
                                action: 'trainingSets'
                            }
                        ]
                    },
                    {
                       text: 'Genetic algorithms',
                       action: 'geneticAlgorithms'
                    },
                    {
                        text: 'Statistics',
                        action: 'statistics'
                    },
                    {
                        text: 'Raw data',
                        action: 'rawData'
                    },
                    {
                        text: 'Web services',
                        action: 'webServices'
                    }
                ]
			}]


        });

        this.callParent(arguments);

    }

});
