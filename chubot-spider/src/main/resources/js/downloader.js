/**
 * Created by zhuyin on 1/30/15.
 */

// casperjs --proxy=proxy.cmcc:8080 tbGoodsScroll.js --url="http://www.jd.com/" --out="jd.html"
var casper = require('casper').create({
    clientScripts: [ // These  scripts will be injected in remote DOM on every request
        // 'include/jquery.js'
    ],
    pageSettings: {
        loadImages: false,        // The WebPage instance used by Casper will
        loadPlugins: false         // use these settings
    },
    verbose: true,
    logLevel: "debug"
});

var url = casper.cli.get("url");
var out = casper.cli.get("out");
var useragent = casper.cli.get("userAgent");
if (url == null) {
    url = "http://s.m.taobao.com/search.htm?q=%E5%AD%95%E5%A6%87%E5%A5%B6%E7%B2%89&spm=41.139785.167729.2";
}
if (out == null) {
    out = "tbGoods";
}
if (useragent == null) {
    useragent = "Mozilla/5.0 (Linux; Android 4.4.2; M812C Build/KVT49L) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.59 Mobile Safari/537.36";
}
casper.echo('userAgent: ' + useragent);
casper.userAgent(useragent);

casper.echo("Casper CLI passed options:");
require("utils").dump(casper.cli.options);


casper.start(url, function () {
    this.emit("page.loaded");
});

casper.run(function () {
    this.echo('Done of ' + url).exit(); // <--- don't forget me!
});

casper.on('page.loaded', function () {
    dumpPageContent(this);
});


function dumpPageContent(casper) {
    casper.then(function () {
        var html = this.getPageContent();
        this.echo("dump page to file " + out);
        require('fs').write(out, html, 'w');
    });
}