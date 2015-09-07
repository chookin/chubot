String.formatmodel = function(str,model){
    for(var k in model){
        var re = new RegExp("{"+k+"}","g");
        str = str.replace(re,model[k]);
    }
    return str;
}

var FIRST_HIDE_STATE = false;
var VailForm = (function(){
    var checkList = [];
    var errDom = {};
    var emailReg = /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i;

    var checkInputVal = function(){
        for(var i = 0, len = checkList.length; i < len; i++){
            var input = checkList[i];
            var label = input.prev();
            if(input.val() != ""){
                label.css("display") == "none" || label.hide();
            }
            else{
                label.css("display") == "none" && label.show();
            }
        }
    }

    var showMsg = function(input, msg){
        var id = input.attr("id");
        if(!msg){
            msg = input.attr("errmsg");
        }
        var type = input.attr("errmsgtype");
        var className = "popup-hint";
        var l = input.offset().left;
        var hitH = 40;
        var t = input.offset().top + input.height() + 12;
        switch(type){
            case "1":
                if(msg.length <= 7){
                    className += " popup-min";
                }
                break;
            case "2":
                className += " popup-top";
                t = input.offset().top - hitH;
                break;
        }
        if(!errDom[id]){
            errDom[id] = $('<div class="' + className + '" style="top:' + t + 'px; left:' + l + 'px;display:none;"><b class="sl"></b><span rel="txt">'+msg+'</span><b class="sr"></b></div>');
            $(document.body).append(errDom[id]);
        }
        else{
            errDom[id][0].className = className;
            errDom[id].css({left:l + "px", top: t + "px"});
        }
        errDom[id].find("[rel='txt']").html(msg);
        errDom[id].show();
    }

    var hideMsg = function(input){
        var id = input.attr("id");
        if(errDom[id]){
            errDom[id].hide();
        }
    }

    var checkFun = function(input){
        var val = input.val();
        var type = input.attr("errortype");
        switch(type){
            case "email":
                if(!emailReg.test(val)){
                    return false;
                }
                break;
            case "notempty":
                if($.trim(val) == ""){
                    return false;
                }
                if(input.attr("minlen") && val.length < Number(input.attr("minlen"))){
                    return false;
                }
                break;
            case "rate":
                if($("#" + input.attr("rate")).val() != val){
                    return false;
                }
                break;
        }

        return true;
    }

    var checkIng = false;
    var ajaxCheck = function(ele, sucCallback, errCallback){
        if(!checkIng){
            checkIng = true;
            $.getScript(ele.attr("isjs") + "&js_return=js_check_back_val&" + ele.attr("name") + "=" + encodeURIComponent(ele.val()), function(){
                checkIng = false;
                if(window["js_check_back_val"]){
                    var res = window["js_check_back_val"];
                    if(!res.state){
                        ele.attr("err", "2");
                        showMsg(ele, res.message);
                        if(errCallback) errCallback();
                    }
                    else{
                        hideMsg(ele);
                        ele.attr("err","");
                        if(sucCallback) sucCallback();
                    }


                    window["js_check_back_val"] = null;

                }
            })
        }
    }

    var bindInput = function(form,input){
        checkList.push(input);

        if(input.attr("errortype") == "rate"){
            $("#" + input.attr("rate")).bind("keyup", function(){
                hideMsg(input);
            })
        }

        input.bind("blur", function(e){
            var ele = $(this);
            if(ele.attr("isjs") || ele.attr("err")){
                var checkRes = checkFun(ele);
                if(checkRes){
                    hideMsg(ele);
                    //$(this).attr("err", "");
                    ele.removeAttr("err");
                    if(ele.attr("isjs")){
                        ajaxCheck(ele);
                    }
                }
                else{
                    showMsg(ele);
                }
            }
        }).bind("focus", function(){
            var ele = $(this);
            ele.removeAttr("err");
            if(ele.attr("isjs")){
                form.find("input[errortype]").each(function(){
                    !$(this).attr("isjs") && hideMsg($(this));
                });
            }

            if(form.attr("rel") == "reg"){
                $("form[rel='login']").find("input[errortype]").each(function(){
                    hideMsg($(this));
                })
            }
            else{
                $("form[rel='reg']").find("input[errortype]").each(function(){
                    hideMsg($(this));
                })
            }

        }).bind("keydown", function(){
            var ele = $(this);
            if(ele.attr("isjs")){
                form.find("input[errortype]").each(function(){
                    hideMsg($(this));
                });
            }
        })
    }

    var _hideTimer;

    return {
        ShowMsg: function(ele, msg){
            showMsg(ele,msg);
        },
        HideMsg: function(ele){
            hideMsg(ele);
        },
        Init: function(){
            $("form[vail]").each(function(i){
                var form = $(this);

                form.find("input[errortype]").each(function(j){
                    bindInput(form, $(this));
                });

                form.bind("submit", function(){
                    if(_hideTimer){
                        window.clearTimeout(_hideTimer);
                    }
                    var res = true;
                    var firstDom;
                    var form = $(this);
                    form.find("input[errortype]").each(function(){
                        var ele = $(this);
                        if(ele.attr("err") == "2"){
                            res = false;
                            firstDom = ele;
                        }
                        else{
                            var checkRes = checkFun(ele);
                            if(checkRes){
                                hideMsg(ele);
                            }else{
                                showMsg(ele);
                                res = false;
                                ele.attr("err", "1");
                                if(!firstDom){
                                    firstDom = ele;
                                }
                            }
                        }
                    });
                    if(!res){
                        if(firstDom.attr("isjs")) {
                            form.find("input[errortype]").each(function(){
                                !$(this).attr("isjs") && hideMsg($(this));
                            });
                            var ele = firstDom;
                            if(ele.attr("err") != "1"){
                                ajaxCheck(ele, function(){
                                    window.setTimeout(function(){
                                        form.submit();
                                    }, 10);
                                });
                            }
                        }
                        firstDom.focus();
                    }

                    if(res){
                        if(form.attr("rel") == "reg"){
                            if(!$("#js_agree_checkbox").attr("checked")){
                                res = false;
                                alert("抱歉，您必须同意使用协议才可以注册！");
                                return false;
                            }
                            if($("#reg_email").attr("err") == undefined){
                                ajaxCheck($("#reg_email"), function(){
                                    window.setTimeout(function(){
                                        form.submit();
                                    }, 10);
                                });
                                return false;
                            }
                            if($("#reg_user_name").attr("err") == undefined){
                                ajaxCheck($("#reg_user_name"), function(){
                                    window.setTimeout(function(){
                                        form.submit();
                                    }, 10);
                                });
                                return false;
                            }
                        }
                    }

                    if(res){
                        if(form.attr("rel") == "reg"){
                            var codeDom = $("#reg_valicode");
                            if(codeDom.length){
                                if(codeDom.attr("err") != "3"){
                                    ajaxCheck(codeDom, function(){
                                        codeDom.attr("err", "3");
                                        form.submit();
                                    }, function(){
                                        $("#js_code_img").attr("src", "http:///?ct=securimage&ac=reg&date=" + new Date().getTime());
                                        window.setTimeout(function(){
                                            codeDom.select();
                                        },50);
                                    });
                                    return false;
                                }
                            }
                        }
                    }
                    if(!res){
                        _hideTimer = window.setTimeout(function(){
                            form.find("input[errortype]").each(function(){
                                var ele = $(this);
                                hideMsg(ele);
                            });
                        },2000);
                    }

                    return res;
                })
            })


            window.setInterval(checkInputVal, 10);
        },
        BindResize: function(){
            $(window).bind("resize", function(){

                $("form[vail]").each(function(i){
                    var form = $(this);
                    form.find("input[errortype]").each(function(){
                        var ele = $(this);
                        hideMsg(ele);
                    });
                });

            });
        }
    }
})();

var MoveClass = (function(){
    var _data = [
            [
                {cl: "nv-sotrage", text: "大容量存储"},
                {cl: "nv-sync", text: "文件同步"},
                {cl: "nv-share", text: "和朋友分享"},
                {cl: "nv-view", text: "在线阅读"}

            ],
            [
                {cl: "nv-safe", text: "安全稳定"},
                {cl: "nv-speed", text: "高速存取"},
                {cl: "nv-platform", text: "多平台访问"},
                {cl: "nv-upload", text: "断点续传"}
            ],
            [
                {cl: "nv-vip", text: "VIP增值服务"},
                {cl: "nv-client", text: "多种客户端"},
                {cl: "nv-circle", text: "资源圈子"},
                {cl: "nv-app", text: "应用市场"}
            ]
        ],
        _temp = '<li style="display:none;"><i class="{cl}" style="cursor:default;"></i><span style="cursor:default;">{text}</span></li>',
        _box, _doms = {}, _active = 0, _left = 154, _old_active,_listenTimer;

    var getDom = function(){
        if(_listenTimer) window.clearTimeout(_listenTimer);
        if(_active >= _data.length){
            _active = 0;
        }
        if(_active < 0){
            _active = _data.length - 1;
        }
        var key = "node_" + _active;
        if(!_doms[key]){
            _doms[key] = [];
            var item = _data[_active];
            for(var i = 0, len = item.length; i < len; i++){
                var n = $(String.formatmodel(_temp, item[i]));
                _box.append(n);
                _doms[key].push(n);
            }
        }

        if(_old_active == undefined){
            _old_active = 0;
        }

        if(_old_active != _active){
            moveHide(_old_active, _active > _old_active, function(){
                var isLeft = _old_active < _active;
                _old_active = _active;
                _box.children().hide();
                moveShow(isLeft);
            });
        }
        else{
            moveShow(_old_active < _active);
            _old_active = _active;
        }
    }

    var moveShow = function(isLeft){
        var key = "node_" + _active;

        if(_doms[key]){
            var arr = [];
            for(var i = 0, len = _doms[key].length; i < len; i++){
                var node = _doms[key][i];
                node.css({left: (_left * i) + "px"});
                var l = _left * i;
                var e;
                if(isLeft){
                    e = l + 616;
                }
                else{
                    e = l - 616;
                }
                arr.push({dom: node,start: e, stop:l, step:2});
                node.css({left: e + "px"}).show();
            }
            moveDoms(arr, function(){
                listen();
            });
        }
    }

    var moveHide = function(k, isLeft, callback){
        var arr = [];
        var key = "node_" + k;
        var item = _data[k];
        if(item){
            for(var i = 0, len = item.length; i < len; i++){
                var node = _doms[key][i];
                var l = Number(node.css("left").replace("px", ""));
                if(isLeft){
                    arr.push({dom: node,start: l, stop:l - 616, step:6});
                }
                else{
                    arr.push({dom: node,start: l, stop:l + 616, step:6});
                }

            }
            moveDoms(arr,function(){
                callback && callback();
            });
        }
    }



    var _cacheArr, _timer, _speed = 3, _moveLen = 9, _callback;
    if(document.all){
        _speed = 1, _moveLen = 14;
    }
    var moveDoms = function(arr, callback){
        if(arr){
            _cacheArr = arr;
        }
        if(callback){
            _callback = callback;
        }
        if(arr && !callback){
            _callback = false;
        }

        _timer = window.setTimeout(function(){
            var endNum = 0;
            for(var i = 0, len = _cacheArr.length; i < len; i++){
                var item = _cacheArr[i];
                if(!item.step){
                    item.step = 1;
                }
                var l = Number(item.dom.css("left").replace("px", ""));



                if(item.start > item.stop){
                    var ml = _moveLen - (i * 2);
                    l = l - (item.step * ml);
                    if(l < item.stop){
                        l = item.stop;
                    }
                }
                else{
                    var ml = _moveLen + (i * 2);
                    l = l + (item.step * ml);
                    if(l > item.stop){
                        l = item.stop;
                    }
                }
                if(l == item.stop){
                    endNum++;
                }
                item.dom.css({left: l + "px"});
            }
            if(endNum != _cacheArr.length){
                moveDoms();
            }
            else{
                window.clearTimeout(_timer);
                if(_callback) _callback();


            }
        }, _speed);
    }


    var listen = function(){
        _listenTimer = window.setTimeout(function(){
            if(_listenTimer){
                window.clearTimeout(_listenTimer);
            }
            MoveClass.Next();

        },8000);
    }

    return {
        Init: function(box){
            _box = box;
            getDom();
        },
        Next: function(){
            if(_box){

                _active++;
                getDom();
            }
        },
        Prev: function(){
            if(_box){
                _active--;
                getDom();
            }
        }
    }
})();


var PageInit = function(){
    window.setTimeout(function(){
        if(document.getElementById('account').value == ''){
            document.getElementById('account').focus();
        }
        else{
            if('' == '300'){
                document.getElementById('account').select();
            }
            else{
                document.getElementById('passwd').focus();
            }
        }
    }, 50)

    MoveClass.Init($('#js_nav_line_box'));
    VailForm.Init();

    var error = '';
    if(error){
        var errShowBox = '' == '300'? $("#account") : $("#passwd");
        VailForm.ShowMsg(errShowBox, error);
        window.setTimeout(function(){
            VailForm.HideMsg(errShowBox);
        },2000);

    }
    else{
        window.setTimeout(function(){
            VailForm.BindResize();
        },20);
    }
}

PageInit();