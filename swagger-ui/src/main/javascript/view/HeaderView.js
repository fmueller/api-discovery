'use strict';

SwaggerUi.Views.HeaderView = Backbone.View.extend({
  events: {
    'click #show-pet-store-icon'    : 'showPetStore',
    'click #show-wordnik-dev-icon'  : 'showWordnikDev',
    'change #input_baseUrl'         : 'loadDefinition'
  },

  oauthProvider: null,

  initialize: function(){
  },

  showPetStore: function(){
    this.trigger('update-swagger-ui', {
      url:'http://petstore.swagger.io/v2/swagger.json'
    });
  },

  showWordnikDev: function(){
    this.trigger('update-swagger-ui', {
      url: 'http://api.wordnik.com/v4/resources.json'
    });
  },

  loadDefinition: function(e) {
    var selectedEntry = $(e.target).children(':selected').val();
    var api_id = selectedEntry.match(/#!\/apis\/([a-z0-9-]+)/);
    if (api_id && api_id.length > 1) {
      api_id = decodeURIComponent(api_id[1]);
      window.location.hash = selectedEntry;
    } else {
      console.error('Invalid api_id provided!');
      return;
    }

    var that = this;
    $.ajax({
      url: window.SUIENV_STORAGE_BASE_URL + '/apps/' + api_id,
      type: 'GET',
      dataType: 'json',
      beforeSend: function(xhr) {
        if (that.oauthProvider) {
          xhr.setRequestHeader('Authorization', 'Bearer ' + that.oauthProvider.getAccessToken());
        }
      },
      error: function() {
        that.trigger('update-swagger-ui', {
          url: window.SUIENV_STORAGE_BASE_URL + '/apps/' + api_id + '/definition'
        });
      },
      success: function(metaData) {
        that.trigger('update-swagger-ui', {
          url: window.SUIENV_STORAGE_BASE_URL + '/apps/' + api_id + '/definition',
          metaData: metaData
        });
      }
    });
  },

  update: function(url, apiKey, trigger){
    if (trigger === undefined) {
      trigger = false;
    }

    if (trigger) {
      this.trigger('update-swagger-ui', {url:url});
    }
  }
});
