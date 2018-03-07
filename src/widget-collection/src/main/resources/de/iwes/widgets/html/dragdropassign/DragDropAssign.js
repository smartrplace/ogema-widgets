/* global GenericWidget */

DragDropAssign.prototype = new GenericWidget();
DragDropAssign.prototype.constructor = DragDropAssign;


function DragDropAssign(servletPath, widgetID) {
    GenericWidget.call(this, servletPath, widgetID);

    this.dragdropassign = $("#" + this.widgetID);
    this.container = this.dragdropassign.find("#container"); //TODO: Rename
    this.assignData = ""; //TODO: Init with {}
    this.templateContainer = "";
    this.templateItem = "";
    this.changes = {};
    this.colCount = -1;
    this.loadTemplate();
}

DragDropAssign.prototype.loadTemplate = function () {

    var me = this;
    //Get template-html
    $.when(
            $.get("/ogema/widget/dragdropassign/TemplateContainer.html") //TODO: Absolute-path, we could run into issues here
            .done(function (data) {
                me.templateContainer = data;
            })
            .fail(function () {
                console.error("DragDropAssign: Could not load template 'TemplateContainer.html'!");
            }),
            $.get("/ogema/widget/dragdropassign/TemplateItem.html") //TODO: Absolute-path, we could run into issues here

            .done(function (data) {
                me.templateItem = data;
            })
            .fail(function () {
                console.error("DragDropAssign: Could not load template 'TemplateItem.html'!");
            }))
            .then(function () { //When all template files are loaded
                me.sendGET();
            });
};

DragDropAssign.prototype.build = function () {
    var me = this;
    $.each(this.assignData.containers, function (index, value) {
        me.buildContainer(value);
    });
    
    //Wrap row-div arround all containers in one line to form rows
    var currCol = 0;
    me.container.children("#coldiv").each(function(index, value) {
        if(currCol === 0 ) {
            $(value).before("<div class=\"row\">");
        }
        else {
            if(currCol >= me.colCount) {
                $(value).after("</div>");
                currCol = 0;
            }
        }
        currCol++;
    });
    
    this.buildItems(this.assignData.items);
};

DragDropAssign.prototype.buildContainer = function (data, parent, z) {
    var me = this;

    if (this.templateContainer === "" || this.templateItem === "") {
        console.error("DragDropAssign (" + this.widgetID + "): Can't build GUI, template-data missing.");
    }
    else {

        if (typeof z === "undefined") {
            z = 0;
        }
        if (typeof parent === "undefined") {
            parent = me.container;
        }       
        var iconString = "";
        if (data.hasOwnProperty("iconLink") && data.iconLink !== null) {
        	var glyphicon = "glyphicon glyphicon-envelope";
        	if (data.iconType != null) {
        		glyphicon = data.iconType;
        	}
        	iconString = "<a target=\"blank\" href=\""+ data.iconLink + 
        		"\"><span class=\"" + glyphicon +"\" style=\"float:right; padding-right:8px;\"></span></a>";
        }
        parent.append(
                this.templateContainer
                .replace("COLCLASS", "col-sm-" + 12 / me.colCount)
                .replace("CONTAINERID", data.id)
                .replace("CONTAINERTITLE", data.name + iconString)
                .replace("ZINDEX", z)
                );
        var selector = parent.find("#" + data.id);
        if (data.bgImagePath !== null && data.bgImagePath !== "" && typeof data.bgImagePath !== "undefined") { //If a background image is set, assign it
            selector.css("background-image", "url('" + data.bgImagePath + "')");
            selector.css("background-size", "contain"); // TODO move to styles?
        }
        if (data.name === "" || typeof data.name === "undefined") { //Don't display containers with empty title
            selector.css("visibility", "hidden");
        }
        if (data.hasOwnProperty("containers") && typeof data.containers !== "undefined" && data.containers !== null) {
            $.each(data.containers, function (index, value) {
                me.buildContainer(value, selector, z + 1);
            });
        }
    }
};


DragDropAssign.prototype.buildItems = function (items) {
    var me = this;
    $.each(items, function (index, value) {
        if (typeof value.container === "undefined" || value.container === null) {
            console.warn("Item '" + value.id + "' does not have any container assigned! Item will not be displayed.");
        }
        else {
            me.container.find("#" + value.container.id).append(
                    me.templateItem
                    .replace("ITEMID", value.id)
                    .replace("ITEMTITLE", value.name)
                    .replace("ITEMICONSOURCE", value.iconPath)
                    );
        }
    });
};

DragDropAssign.prototype.moveItem = function (itemDOM, targetDOM) {
    if (typeof itemDOM === "undefined" || typeof targetDOM === "undefined") {
        console.error("Invalid arguments!");
        throw "invalid";
        return;
    }
    else {
        if ($(itemDOM).parent().attr("id") !== $(targetDOM).attr("id")) {//Only move if destination is new container
            try {
                this.changes.item = this.getItem(itemDOM);
                this.changes.from = this.getContainer(this.assignData, $(itemDOM).parent());
                this.changes.to = this.getContainer(this.assignData, targetDOM);

                //Update frontend
                this.changes.item.container = this.changes.to; //JSON-update
                $(targetDOM).append(itemDOM); //DOM-update

                //Inform backend about update
                this.sendPOST();
            }
            catch (err) {
                console.error("Error while updating data: " + err);
            }
            this.changes = {};
        }
    }
};

//Queries correlating json-object from dom-object by id
DragDropAssign.prototype.getContainer = function (data, dom) {
    var me = this;

    if (typeof data === "undefined" || typeof dom === "undefined") {
        console.error("Invalid arguments!");
        throw "invalid";
        return;
    }
    else {
        var result = {};

        dom = $(dom); //cast to jquery
        if (data.hasOwnProperty("id") && dom.attr("id") === data.id) {
            result.data = data;
        }
        else {
            checkChildren(data, dom, result);
            return result.data;
        }

        if (typeof result.data === "undefined") {
            throw "container_not_found";
        }
        else {
            return result.data;
        }
    }

    function checkChildren(data, dom, result) {
        if (data.hasOwnProperty("containers") && data.containers !== null) {
            $.each(data.containers, function (index, value) {
                result.data = me.getContainer(value, dom);
                if (typeof result.data !== "undefined") {
                    return false;
                }
            });
        }
    }
};

//Queries correlating json-object from dom-object by id
DragDropAssign.prototype.getItem = function (dom) {
    if (typeof dom === "undefined") {
        console.error("Invalid arguments!");
        throw "invalid";
        return;
    }
    else {
        dom = $(dom);
        var item;
        $.each(this.assignData.items, function (index, value) {
            if (dom.attr("id") === value.id) {
                item = value;
                return;
            }
        });

        if (typeof item === "undefined") {
            throw "item_not_found";
        }
        else {
            return item;
        }
    }
};

DragDropAssign.prototype.update = function (data) {
    if (data.hasOwnProperty("assignData")) {
        if (this.assignData != data.assignData || this.colCount != data.colCount) {
            if (!data.hasOwnProperty("colCount") || typeof data.colCount === "undefined") { //Failsafe
                this.colCount = 2;
            }
            else {
                if (data.colCount > 12) { //Limit columns for layout to 12 (bootstrap-limit)
                    this.colCount = 12;
                }
                else {
                    this.colCount = data.colCount;
                }
            }
            this.assignData = data.assignData;
            this.container.html("");
            this.build();
            this.initDD();
        }
    }
};

DragDropAssign.prototype.initDD = function () {
    var me = this;

    //Make all items draggable
    $.each(this.container.find("li"), function (index, value) {
        $(value).draggable({
            revert: "invalid", // when not dropped, the item will revert back to its initial position
            containment: "document",
            helper: "clone",
            cursor: "move"
        });
    });
    //Make all containers droppable
    $.each(this.container.find(".dragdrop-container"), function (index, value) {
        if ($(value).css("visibility") !== "hidden") { //Only make visible containers droppable
            $(value).droppable({
                activeClass: "custom-state-active",
                greedy: true, //Enables use of nested containers
                drop: function (event, ui) {
                    me.moveItem(ui.draggable, event.target);
                }
            });
        }
    });
};

DragDropAssign.prototype.getSubmitData = function () {
    return this.changes;
};

/*
 //Mpdify container-background js-side. TODO: Inform backend about change!!
 DragDropAssign.prototype.setContainerBackground = function(containerID, bgImagePath) {
 if(typeof containerDOM === "undefined" || typeof bgImagePath === "undefined") {
 console.error("Invalid arguments!");
 throw "invalid";
 }
 else {
 var containerDOM = this.container.find("#" + containerID);
 if(typeof containerDOM === "undefined") { //If jquery can't find container by id
 console.error("Could not find container with id " + containerID + "!");
 throw "invalid";
 }
 else {
 this.getContainer(this.assignData, containerDOM).bgImagePath = bgImagePath; //TODO: Failsafe for typeof bgImagePath === "undefined" or null
 containerDOM.css("background-image", bgImagePath);
 }
 }
 };
 */