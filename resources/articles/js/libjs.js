window.onload = function(){

    // reload page when orientation change
    $( window ).on( "orientationchange", function( event ) {
      location.reload();
    });

    // set content padding-top & top paddding-right
    setPadding();

    // set class for images
    bigImages();

    // bind click animation on images
    bindClickImage();

    // bind click animation on videos
    bindClickVideo();

    // randomize position of notes in the margin
    randomizePositionNotes();

    // draw svg lines between notes and paragraph
    drawLines();
};



function setPadding(){
    var tophheight = $(".top").height();
    $(".content").css("padding-top",tophheight + 80);

    var paddright = $(".container").width() - $(".top").width() + 20;
    $(".top").css("padding-right", paddright);
};

function getElementLeft(elm){
    var x = 0;
    x = elm.offsetLeft;
    elm = elm.offsetParent;

    while(elm != null){
        x = parseInt(x) + parseInt(elm.offsetLeft);
        elm = elm.offsetParent;
    }
    return x;
};

function getElementTop(elm){
    var y = 0;
    console.log("e: " + elm);
    y = elm.offsetTop;
    elm = elm.offsetParent;

    while(elm != null){
        y = parseInt(y) + parseInt(elm.offsetTop);
        elm = elm.offsetParent;
    }
    return y;
};

// if page doesnt contains paragraph -> display the entire images
function bigImages(){
    if( $(".content > p").length == 0){
        $(".border").addClass("border_big");
        $( "img" ).removeClass("miniature").addClass("big_image");
    }
};

function bindClickImage(){
    $( "img.miniature" ).bind( "click tap", function() {
        h = 250;
        w = 300;
        t = getElementTop(this) - h + this.offsetHeight - 2;
        l = getElementLeft(this) - 4;

        $("#imgpopup").attr("src",$(this).attr("src"));
        $("#popup_img").css({"visibility" : "visible", "width" : w + "px", "height" : h + "px", "top" : t +"px", "left" : l + "px","z-index" : "50"});

    });

    $("#imgpopup").bind( "click tap", function(){
        $("#imgpopup").attr("src","");
        $("#popup_img").css({"visibility" : "hide", "width" : 0 + "px", "height" : 0 + "px", "top" : 0 +"px", "left" : 0 + "px"});
    });
};

function bindClickVideo(){
    $(".iframe-wrapper").bind( "click tap", function(){
        // open in a new tab
        window.open($(this).find("iframe").attr("src"));
    });
};

function randomizePositionNotes(){
    var notes = $(".marge .inner");

    for(var i=0; i < notes.length; i++){
        var n = $(notes[i]);
        // linked element
        var elem = n.parent().next();
        var elem_h = elem.height();

        // random height of the note (between -45 and elem.heigth+45)
        //var h = Math.floor((Math.random() * (elem_h+45)) - 45);
        var h = Math.floor(elem_h - 45);
        if( h < 0 ) h = 45;
        n.css("top",h + "px");
    }
};

function drawLines(){
    var notes = $(".marge .inner p");
    var body = $("body");


    // create new svg element
    var svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    svg.setAttribute('width', body.width());
    svg.setAttribute('height', body.height());

    // create lines
    for(var i=0; i < notes.length; i++){
        var n = $(notes[i]);
        var elem = n.parent().parent().next();


        // no lines with "big images"
        if( elem.hasClass("border_big") ) continue;


        var n_top = getElementTop(n[0]) + 5;
        var n_left = getElementLeft(n[0]) - 25;

        // linked element
        var elem_top = getElementTop(elem[0]) + 5;
        var elem_left = getElementLeft(elem[0]) + elem.width() - 25;

        // create path svg element
        var line = document.createElementNS("http://www.w3.org/2000/svg", "path");

        // positon of control point of the curve
        var x1 = elem_left + 35;
        var y1 = elem_top + 20;

        var d = "M" + elem_left + " " + elem_top + " Q " + x1 + " " + y1 + " " + n_left + " " + n_top;
        line.setAttributeNS(null,"d",d);
        line.setAttributeNS(null,"class","line");
        svg.appendChild(line);

    }

    // append svg element to the body
    $(".lines").append(svg);
}