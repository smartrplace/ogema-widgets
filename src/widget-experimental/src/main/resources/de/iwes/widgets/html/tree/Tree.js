Tree.prototype = new GenericWidget();
Tree.prototype.constructor = Tree;

function Tree(servletPath, widgetID) {

    GenericWidget.call(this, servletPath, widgetID);
    this.objectRepresentation = {};
    this.sendGET();
}
;

Tree.prototype.initBootstrapTree = function() {
	var tree = this;
	// FIXME apply only to current tree!
    $('#' + tree.widgetID +' .tree li:has(ul)').addClass('parent_li').find(' > span').attr('title', 'Collapse this branch');
    $('#' + tree.widgetID +' .tree li.parent_li>span').on('click', function (e) {
        var children = $(this).parent('li.parent_li').find(' > ul > li');
        if (children.is(":visible")) {
            children.hide('fast');
            $(this).attr('title', 'Expand this branch').find(' > i').addClass('glyphicon-plus-sign').removeClass('glyphicon-minus-sign');
        } else {
            children.show('fast');
            $(this).attr('title', 'Collapse this branch').find(' > i').addClass('glyphicon-minus-sign').removeClass('glyphicon-plus-sign');
        }
        var id = this.id;
        var data;
        if (tree.objectRepresentation.hasOwnProperty(id))
        	data = tree.objectRepresentation[id];
        else
        	data = {};
        var html = "";
        $.each(data, function(key,value) {
        	html += "<span><b>" + key + ":</b> " + value + "</span><br>";
        });
        e.stopPropagation();
    });
    $('#' + tree.widgetID +' .tree li>span').on('click', function (e) {
    	var id = this.id;
        var data;
        if (tree.objectRepresentation.hasOwnProperty(id))
        	data = tree.objectRepresentation[id];
        else
        	data = {};
        var html = "";
        $.each(data, function(key,value) {
        	html += "<span><b>" + key + ":</b> " + value + "</span><br>";
        });
        $("#" + tree.widgetID + " #tableBody").html(html);
    });
}

Tree.prototype.update = function (data) {
    console.log("Treeroot: ", data);
    this.objectRepresentation = {};
    var tree = this;
    var html = "";
    if (data.hasOwnProperty("root")) {
        var root = data.root;
        var id = "root";
        if (root.hasOwnProperty("id"))
        	id = root.id;
        
        html += "<li><span class='" + root.css + "' id='" + id + "'><i class='" + root.glyphicon + "'>" +
                "</i> " + root.name + "</span>";
        $.each(root, function (index, value) {
            console.log("-");
            if (index === "data") {
                html += treeLeafToHTML(value);
            }
            var lastIndex = html.lastIndexOf("</ul>");
            var replaced = false;
            if(lastIndex > 0){
                html = html.substring(0, lastIndex);
                replaced = true;
                
            }
            
//            console.log("replaced: ", replaced);
            if (index === "childs") {
                html += treeNodeToHTML(value, tree);
            }
            
            if(replaced){
                html += "</ul>";
            }
            
        });

        $("#" + this.widgetID + " #ogemaTree").html(html);
        this.initBootstrapTree();

    } else {
	$("#" + this.widgetID + " #ogemaTree").html("");
    }
};

function treeNodeToHTML(node, tree) {

    var glyphicon = "glyphicon glyphicon-user";
//    if (node.hasOwnProperty("glyphicon")) {
//        glyphicon = node.glyphicon;
//        delete node.glyphicon;
//    }

    var css = "";
//    if (node.hasOwnProperty("css")) {
//        css = node.css;
//        delete node.css;
//    }

    var html = "<ul>";
    $.each(node, function (index, value) {
        var id = index;
        var name = value.name;
        glyphicon = value.glyphicon;
        css = value.css;

        html += "<li style=\"display:none;\"><span class='" + css + "' id='" + id +"'><i class='" + glyphicon + "'>" +
                "</i> " + name + "</span>";
        if (value.hasOwnProperty("objectRepresentation"))
        	tree.objectRepresentation[id]=value.objectRepresentation;

        if (value.hasOwnProperty("data")) {
            var leaf = value.data;
            html += treeLeafToHTML(leaf, tree);
        }
        if (value.hasOwnProperty("childs")) {
            html += treeNodeToHTML(value.childs, tree);
        }

    });
    html += "</ul>";
    return html;
}
;

function treeLeafToHTML(leaf, tree) {
    var html = "<ul>";
    var glyphicon = "glyphicon glyphicon-user";
    var css = "";

//    if (leaf.hasOwnProperty("glyphicon")) {
//        glyphicon = leaf.glyphicon;
//        delete leaf.glyphicon;
//    }
//    var css = "";
//    if (leaf.hasOwnProperty("css")) {
//        css = leaf.css;
//        delete leaf.css;
//    }

    $.each(leaf, function (index, value) {
    	var name = value.name;
    	glyphicon = value.glyphicon;
    	css = value.css;

        html += "<li style=\"display:none;\"><span class='" + css + "' id='" + index + "'><i class='" + glyphicon + "'></i>" +
                index + "</span><a href=''>  " + name + "</a></li>\n";
        if (value.hasOwnProperty("objectRepresentation"))
        	tree.objectRepresentation[id]=value.objectRepresentation;
    });
    html += "</ul>";
    return html;
};

// XXX ?
Tree.prototype.getSubmitData = function () {
    var data = 5;
    return data;
};
