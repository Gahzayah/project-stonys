/**
 * 
 * Contains follow Functions:
 * 
 * function debugXHR(data)
 * function errorXHR(data)
 * function key_toggle_sidebar(e)
 */

/* @version 2.1 fixedMenu
 * @author Lucas Forchino
 * @webSite: http://www.jqueryload.com
 * jquery top fixed menu
 */

//BlurryMenu action
var BlurryMenu = function () {

//configure the menu style
    self.menuWidth = 300;
    self.animationDuration = 500;
    self.animationStyle = "easeOutQuart";
    self.blurStrength = 16;
    //checkvar for the displaying state
    self.menuOpened = false;
    return {
        //animate closing the menu and the menu content
        closeMenu: function () {
            if (self.menuOpened) {
                $("#menu").animate({
                    width: 0

                }, {
                    duration: self.animationDuration,
                    specialEasing: {
                        width: self.animationStyle
                    }
                });
                $("div.menu-content").animate({
                    width: 0
                }, {
                    duration: self.animationDuration,
                    specialEasing: {
                        width: self.animationStyle
                    },
                    complete: function () {
                        $("div.menu-content").css("position", "relative");
                        self.menuOpened = false;
                    }
                });
            }
        },
        //preloading, save page to canvas to image to blurred image
        preloadMenu: function () {

            /**
             * Bad Workaround because the Rendering for the Sidebar works only
             * for Document Sizes lower than 1200px.
             * 16. August 2015
             */
            if (getDocumentHeight() < 1200) {
                html2canvas(document.body, {
                    onrendered: function (canvas) {

                        var context = canvas.getContext('2d');
                        var dataURL = canvas.toDataURL();
                        $("#captured-image").attr('src', dataURL);
                        setTimeout(function () {
                            boxBlurImage('captured-image', 'blurred-bg-canvas', this.blurStrength, false, 2);
                        }, 200);
                    },
                    logging: false,
                    height: 1200
                });
            }




        },
        //animate opening the menu and the menu content
        openMenu: function () {
            if (!self.menuOpened) {
                $("div.menu-content").css("position", "fixed");
                $("div.menu-content").css("width", 0);
                $("#menu").height($(document).height());
                $("div.menu-content").animate({
                    width: self.menuWidth
                }, {
                    duration: self.animationDuration,
                    specialEasing: {
                        width: self.animationStyle
                    }

                });
                $("#menu").animate({
                    width: self.menuWidth
                }, {
                    duration: self.animationDuration,
                    specialEasing: {
                        width: self.animationStyle
                    },
                    complete: function () {
                        self.menuOpened = true;
                    }
                });
            }
        }

    };
}();
(function ($) {
    $.fn.fixedMenu = function () {
        return this.each(function () {
            var linkClicked = false;
            var menu = $(this);
            $('body').bind('click', function () {

                if (menu.find('.active').size() > 0 && !linkClicked) {
                    menu.find('.active').removeClass('active');
                } else {
                    linkClicked = false;
                }
            });
            menu.find('ul li > a').bind('click', function () {
                linkClicked = true;
                if ($(this).parent().hasClass('active')) {
                    $(this).parent().removeClass('active');
                } else {
                    $(this).parent().parent().find('.active').removeClass('active');
                    $(this).parent().addClass('active');
                }
            })
        });
    }
})(jQuery);
/**
 * Highlight the Focus on Checkboxes
 * @author http://html.cita.illinois.edu/nav/form/checkbox/index.php?example=5
 * @param {type} param
 */
$(document).ready(function () {

    /* ===================== CHECKBOX - FOCUS FiX =========================== */
// Add the "focus" value to class attribute 
    $('.checkbox input').focusin(function () {
        $('.checkbox label').addClass('label-focus');
    });
    // Remove the "focus" value to class attribute 
    $('.checkbox input').focusout(function () {
        $('.checkbox label').removeClass('label-focus');
    });
    /**
     * @author ymc-thzi https://github.com/ymc-thzi/blurry-menu
     * @param {type} param
     */
    //Preloading for performance reason. Here the complete page will be rendered
    //as a screenshot and the image will be blurred after rendering
    //BlurryMenu.preloadMenu();
    //Just close the menu when window gets resized to avoid some display bugs
//    $(window).resize(function () {
//        BlurryMenu.closeMenu();
//    });
//    //Click on everything closes the menu except clicks on the menu itself
//    $(document).bind('click', function (e) {
//        if ($(e.target).closest('#menu').length === 0) {
//            BlurryMenu.closeMenu();
//        }
//    });




    /* ===================== Configure Datatable ============================ */
//    $('#tbl-ImageOverview').DataTable({
//        fixedHeader: false,
//        lengthChange: false,
//        paging: false,
//        deferRender: false, // only load Content when need JS or Ajax Source
//        ordering: false,
//        processing: false, // Not work until now...
//        searching: false,
//        stateSave: false
////        deferLoading: 57
////        lengthMenu: [ [500, 1000, 5000, -1], [500, 1000, 5000, "All"] ]
//    });
    /* ====================================================================== */

    /* ===================== Loading Screen ================================= */
    $(".pageloader").fadeOut("slow");
    /* ====================================================================== */

    /* ===================== Regist Key Handler ============================= */
    // Invoke doc_keyUp Function ( Alt + X )
    document.addEventListener('keyup', key_toggle_sidebar, false);
    /* ====================================================================== */

});
//window.onload = function () {
//    setTimeout(function () {
//        var t = performance.timing;
//        console.log(t.loadEventEnd - t.responseEnd);
//        $(".rendertime").html("RenderTime: " + (t.loadEventEnd - t.responseEnd));
//    }, 0);
//}


var savekeystate = false;
// define a handler
function key_toggle_sidebar(e) {

    // this would test for whichever key is 40 and the ctrl key at the same time
    if (e.altKey && e.keyCode == 88) {
        // call your function to do the thing
        if (savekeystate) {
            BlurryMenu.closeMenu();
            savekeystate = false;
        } else {
            BlurryMenu.openMenu();
            savekeystate = true;
        }


    }
}


/**
 * 
 * @param {type} data
 * @returns {undefined}
 */
function debugXHR(data) {
    var statusElement = document.getElementById("status");
    switch (data.status) {
        case "begin"   :
            statusElement.innerHTML = "Sent request. Waiting for response...";
        case "complete":
            statusElement.innerHTML = "Response received";
        case "success" :
            statusElement.innerHTML = "Rendered successfully";
    }
}

/**
 * 
 * @param {type} data
 * @returns {undefined}
 */
function errorXHR(data) {
    var statusElement = document.getElementById("status");
    statusElement.innerHTML = "<b>Error</b> <i>" + data.status + " "
            + data.description + "</i>";
}

/**
 * Get the correct Browser Height.
 * http://stackoverflow.com/questions/1145850/how-to-get-height-of-entire-document-with-javascript
 * @returns {Number}
 */
function getDocumentHeight() {
    var body = document.body, html = document.documentElement;
    var height = Math.max(body.scrollHeight, body.offsetHeight,
            html.clientHeight, html.scrollHeight, html.offsetHeight);
    return height;
}