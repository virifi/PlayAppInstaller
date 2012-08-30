$(function() {
    js_scraper.log(js_scraper.get_url());
    js_scraper.set_wait_millis(1000);
    js_scraper.scrape({
        "play.google.com/store$" : function () {
            var name_span = $('span.gbts:first');
            if (name_span.size() < 1) {
                js_scraper.scraping_error("cannot determine your name");
                return;
            }
            var name = name_span.text();
            js_scraper.log(name);
            if (name == "+You") {
                js_scraper.scraping_error("login_error");
                return;
            }
            var packageName = js_scraper.get_argument("app_package_name");
            if (packageName == null) {
                js_scraper.scraping_error("cannot get package name");
                return;
            }
            js_scraper.load_url("https://play.google.com/store/apps/details?id=" + packageName); 
        },
        "store/apps/details" : function () {
            setTimeout(function () {
                js_scraper.click_element($('.buy-button-price'));
                setTimeout(function () {
                    js_scraper.click_element($('#co-dialog-complete-button'));
                    js_scraper.scraping_finished("Installing app");
                }, 4000);
                js_scraper.progress_message("Wait for 4 seconds");
            }, 4000);
            js_scraper.progress_message("Wait for 4 seconds");
        },
    }, function () {
        js_scraper.scraping_error("Unknown url\n" + url);
    });
});
