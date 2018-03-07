EmptyWidget.prototype = new GenericWidget();
EmptyWidget.prototype.constructor = EmptyWidget;


function EmptyWidget(servletPath, widgetID) {

    GenericWidget.call(this, servletPath, widgetID);
    this.sendGET();
}
