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
    var app_id = $(e.target).children(':selected').val();
    var that = this;
    $.ajax({
      url: window.SUIENV_STORAGE_BASE_URL + '/apps/' + app_id,
      type: 'GET',
      dataType: 'json',
      beforeSend: function(xhr){
        if (that.oauthProvider){xhr.setRequestHeader('Authorization', 'Bearer ' + that.oauthProvider.getAccessToken());}
      },
      error: function(){
        that.trigger('update-swagger-ui', {
          url: window.SUIENV_STORAGE_BASE_URL + '/apps/' + app_id + '/definition'
        });
      },
      success: function(metaData) {
        that.trigger('update-swagger-ui', {
          url: window.SUIENV_STORAGE_BASE_URL + '/apps/' + app_id + '/definition',
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
