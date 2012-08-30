var js_scraper = (function () {
    // 次のページに遷移する前に、ScrapingFragmentのloadedメンバをfalseにする
    $(window).bind('beforeunload', function(event) {
        __webViewInterface.prepare();
    });

    var wait_millis = 0;

    function set_wait_millis(millis) {
        wait_millis = millis;
    }

    function log(str) {
        __webViewInterface.log(str);
    }
    function print_source() {
        __webViewInterface.log($('body').html());
    }
    function get_url() {
        return window.location.href;
    }
    function load_url(url) {
        setTimeout(function () {
            window.location.href = url;
        }, wait_millis);
    }
    function scraping_error(message) {
        __webViewInterface.onScrapingError(message);
    }
    function scraping_finished(message, obj) {
        put_result(obj);
        __webViewInterface.onScrapingFinished(message);
    }
    function pagestep(max_step, cur_step) {
        __webViewInterface.onPageStepChanged(max_step, cur_step);
    }
    function get_argument(key) {
        return __webViewInterface.getBundleString(key);
    }
    function scrape(scrape_obj, err_func) {
        var matched = false;
        for (var match_str in scrape_obj) {
            if (get_url().match(new RegExp(match_str))) {
                var match_func = scrape_obj[match_str];
                if (typeof match_func != "function") {
                    log("js_scraper : " + match_str + " is not a function");
                    return;
                }
                match_func();
                break;
            }
        }
        if (!matched && err_func != null) {
            if (typeof err_func != "function") {
                log("js_scraper : err_func is not a function");
            } else {
                err_func();
            }
        }
    }
    function get_temp_string(key) {
        return __webViewInterface.getTempString(key);
    }
    function put_temp_string(key, val) {
        __webViewInterface.putTempString(key, val);
    }
    function progress_message(message) {
        __webViewInterface.onProgressMessage(message);
    }
    function click_element(elm) {
        var offset = elm.offset();
        var left = offset.left;
        var top = offset.top;
        var width = elm.width();
        var height = elm.height();
        var window_width = $(window).width();
        var window_height = $(window).height();
        __webViewInterface.clickElement(window_width, window_height, left, top, width, height);
    }
    function click_element_async(elm, callback) {
        setTimeout(function () {
            if (elm.size() < 1) {
                callback('element does not exist');
            }
            click_element(elm);
            callback(null);
        }, wait_millis);
    }

    // private
    function put_result(obj) {
        for (var key in obj) {
            var val = obj[key];
            if (val == null) {
                continue;
            } else if (typeof val != "string") {
                __webViewInterface.setResultString(key, val.toString());
            } else {
                __webViewInterface.setResultString(key, val);
            }
        }
    }

    return {
        "set_wait_millis" : set_wait_millis,
        "log" : log,
        "print_source" : print_source,
        "get_url" : get_url,
        "load_url" : load_url,
        "scraping_error" : scraping_error,
        "scraping_finished" : scraping_finished,
        "pagestep" : pagestep,
        "get_argument" : get_argument,
        "scrape" : scrape,
        "get_temp_string" : get_temp_string,
        "put_temp_string" : put_temp_string,
        "progress_message" : progress_message,
        "click_element" : click_element,
        "click_element_async" : click_element_async
    };
}());
