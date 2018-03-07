Accordion.prototype = new GenericWidget();
Accordion.prototype.constructor = Accordion;


function Accordion(servletPath, widgetID) {
    var w = this;

    GenericWidget.call(this, servletPath, widgetID);

    this.accordion = $("#" + this.widgetID + " #accordion");
    this.items = [];
    this.addListener();
    this.collapsibles = [];
    this.titles = [];
    this.clickedItem = -1;

    //Get template-html
    $.get("/ogema/widget/accordion/TemplateItem.html")
            .done(function (data) {
                w.templateItem = data;
                w.templateItem.replace("data-parent=\"#accordion\"", "data-parent=\"" + this.widgetID + "\"");
                w.sendGET();
            })
            .fail(function () {
                console.error("Accordion: Could not load accordion-template 'TemplateItem.html'!");
            });
}

Accordion.prototype.update = function (data, oldRequest, subwidgetsToBeRemoved) {

    if (data.hasOwnProperty("css")) {
        this.accordion.removeAttr("style");
        this.accordion.css(data.css);
    }

    if (data.hasOwnProperty("visible")) {
        this.setVisibility(data.visible);
    }

    if (data.hasOwnProperty("hideInactive")) {
        this.hideInactive = data.hideInactive;
    }

    if (data.hasOwnProperty("items")) {

        var body = "";
        var w = this;
        var final = "";
        var loadNewWidgets = this.dataChanged(data, oldRequest, subwidgetsToBeRemoved);
        if (loadNewWidgets) {
            this.items = data.items;

            this.collapsibles = [];
            this.titles = [];
            this.clickedItem = [];

            w.accordion.html("");

            for (var i = 0; i < this.items.length; i++) {
                body = "";

                //check for item-type and write to body
                switch (this.items[i].type) {
                    //case "HTML":  // default case
                    //    body = this.items[i].data;
                    //   break;
                    case "PAGE":
                        body = "<div id=\"page" + i + "\">Loading external content ...</div>";

                        (function (i) {
                            $.get(w.items[i].data)
                                    .success(function (data) {
                                        w.accordion.find("#page" + i).replaceWith(data);
                                    })
                                    .fail(function () {
                                        w.accordion.find("#page" + i).replaceWith("<p>ERROR: Could not load external content!</p>");
                                    });
                        })(i);
                        break;
                        //case "WIDGET":  // default case
                        //    body = "<div id=\"" + this.items[i].data + "\"></div>";
                        //    break;
                    default:
                        body = this.items[i].data;
                }
                var itemID = w.widgetID + "_" + i;
                var collapseID = "collapse_" + itemID;
                var titleCollapseID = "titleCollapse_" + itemID;


                //Replace TITLE, TEMPLATE_BODY, collapse-id, href
                final = w.templateItem
                        .replace("TITLE", w.items[i].title)
                        .replace("TEMPLATE_BODY", body)
                        .replace("id=\"titleCollapse\"", "id=\"" + titleCollapseID + "\"")
                        .replace("id=\"collapse\"", "id=\"" + collapseID + "\"");
                if (w.items[i].expanded === true) {
                    final.replace("class=\"panel-collapse collapse\"", "class=\"panel-collapse collapse in\"");
                }
                w.accordion.append(final);
                var el = w.accordion.find("#" + titleCollapseID);
                w.titles.push(el);
                w.collapsibles.push(w.accordion.find("#" + collapseID));
                el.get(0).style.cursor = "pointer";
                if (w.items[i].hasOwnProperty("tooltip"))
                	el.get(0).title = w.items[i].tooltip;
            }

            this.addClickListener();
        }

    }
    return loadNewWidgets;

};

Accordion.prototype.toggleItemCall = function (item) {
    var w = this;
    return function () {
        w.clickedItem = item;
        item.collapse('toggle');
    };
};

Accordion.prototype.addListener = function () { //Listen for tab-change and trigger this.sendGET()
    var w = this;
    $(document).ready(function () {
    	// FIXME was that needed? 
//        w.accordion.on('hide.bs.collapse', function () {
//            w.sendGET();
//        });
//        w.accordion.on('show.bs.collapse', function () {
//            w.sendGET();
//        });
        w.accordion.on('shown.bs.collapse', function () {
            if (w.hideInactive && w.clickedItem !== -1) {
                $.each(w.collapsibles, function (index, value) {
                    if (value != w.clickedItem)
                        value.collapse('hide');
                });
            }
        });

    });
};

Accordion.prototype.addClickListener = function () {
    var w = this;
    $(document).ready(function () {
        $.each(w.titles, function (index, value) {
            value.click(w.toggleItemCall(w.collapsibles[index]));
        });
    });
};

Accordion.prototype.setVisibility = function (visible) {
    if (visible) {
        this.accordion.show();
    }
    else {
        this.accordion.hide();
    }
};

Accordion.prototype.dataChanged = function (newRequest, oldRequest, subwidgetsToBeRemoved) {
    if (!oldRequest || !oldRequest.hasOwnProperty("items")) {
        if (newRequest.hasOwnProperty("items"))
            return true;
        else
            return false;
    }
    if (!newRequest.hasOwnProperty("items")) {
        if (oldRequest.hasOwnProperty("subWidgets")) {
            subwidgetsToBeRemoved.push.apply(subwidgetsToBeRemoved, oldRequest.subWidgets);
        }
        return false;
    }
    // check whether old and new items are equal: first guess: check only title and id
    if (oldRequest.items.length !== newRequest.items.length) {
        if (oldRequest.hasOwnProperty("subWidgets")) {
            subwidgetsToBeRemoved.push.apply(subwidgetsToBeRemoved, oldRequest.subWidgets);
        }
        return true;
    }
    for (var i = 0; i < oldRequest.items.length; i++) {
        if (oldRequest.items[i].title !== newRequest.items[i].title || oldRequest.items[i].data !== newRequest.items[i].data) {
            if (oldRequest.hasOwnProperty("subWidgets")) {
                subwidgetsToBeRemoved.push.apply(subwidgetsToBeRemoved, oldRequest.subWidgets);
            }
            return true;
        }
    }
    return false;
};



