package beriain.atob;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MainActivity extends AppCompatActivity {

  private SmsBroadcastReceiver smsBroadcastReceiver;
  public static final String MOBILE_NUMBER = "+447723490252";

  SharedPreferences preferences;
  String home =
      "<html><head><title>atob</title><meta charset=\"utf-8\"><meta name=\"viewport\""
          + "content=\"width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1\">"
          + "<style>body{margin:0;text-align: center;font-family: sans-serif;}img{margin-top:50px;"
          + "width:50%;}h2{margin-top:20px;}</style></head><body><img src=\"file:///android_asset/logo.png\" /><h2>atob"
          + "</h2><h3>a text only browser</h3></body></html>";
  String darkHome =
      "<html><head><title>atob</title><meta charset=\"utf-8\"><meta name=\"viewport\""
          + "content=\"width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1\">"
          + "<style>html{color:#cccccc;background:#444444;}a{color:#2196F3;}body{margin:0;text-align:"
          + "center;font-family: sans-serif;}img{margin-top:50px;width:50%;}h2{margin-top:20px;}"
          + "</style></head><body><img src=\"file:///android_asset/logo.png\" /><h2>atob</h2><h3>a text only browser"
          + "</h3></body></html>";
  ArrayList<historyEntry> history;

  JavaScriptInterface jsInterface = new JavaScriptInterface();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // Read history
    history = readHistoryFromInternalStorage();

    // WE INTEND TO READ SMS

    IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
    filter.setPriority(1000); // This might be too high
    this.smsBroadcastReceiver = new SmsBroadcastReceiver(MOBILE_NUMBER);
    registerReceiver(this.smsBroadcastReceiver, filter);

    Intent inte = this.getIntent();

    WebView wv = (WebView) findViewById(R.id.webView);
    wv.getSettings().setJavaScriptEnabled(true);
    wv.addJavascriptInterface(jsInterface, "JSInterface");

    preferences = PreferenceManager.getDefaultSharedPreferences(this);
    // SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    final String searchEngine = preferences.getString("search", "1");

    if (inte.getAction() == Intent.ACTION_VIEW) {
      Loader l = new Loader();
      l.url = inte.getData().toString();
      l.execute();
    } else {
      // wv.loadUrl("file:///android_asset/home.html");
      if (preferences.getString("themes", "1").compareToIgnoreCase(("1")) == 0) {
        if (history.isEmpty()) wv.loadDataWithBaseURL("", home, "text/html", "utf-8", "");
        else wv.loadDataWithBaseURL("", buildHistory(), "text/html", "utf-8", "");
      } else {
        wv.setBackgroundColor(Color.DKGRAY);
        wv.loadDataWithBaseURL("", darkHome, "text/html", "utf-8", "");
      }
    }

    // http://data.iana.org/TLD/tlds-alpha-by-domain.txt
    // # Version 2017011601, Last Updated Tue Jan 17 07:07:02 2017 UTC
    // tr "\n" "|" < tlds-alpha-by-domain.txt
    // tr A-Z a-z < tlds-alpha-by-domain.txt
    final Pattern tlds =
        Pattern.compile(
            "[.](aaa|aarp|abarth|abb|abbott|abbvie|abc|able|abogado"
                + "|abudhabi|ac|academy|accenture|accountant|accountants|aco|active|actor|ad|adac|ads|adult"
                + "|ae|aeg|aero|aetna|af|afamilycompany|afl|ag|agakhan|agency|ai|aig|aigo|airbus|airforce"
                + "|airtel|akdn|al|alfaromeo|alibaba|alipay|allfinanz|allstate|ally|alsace|alstom|am"
                + "|americanexpress|americanfamily|amex|amfam|amica|amsterdam|analytics|android|anquan|anz"
                + "|ao|aol|apartments|app|apple|aq|aquarelle|ar|aramco|archi|army|arpa|art|arte|as|asda|asia"
                + "|associates|at|athleta|attorney|au|auction|audi|audible|audio|auspost|author|auto|autos"
                + "|avianca|aw|aws|ax|axa|az|azure|ba|baby|baidu|banamex|bananarepublic|band|bank|bar"
                + "|barcelona|barclaycard|barclays|barefoot|bargains|baseball|basketball|bauhaus|bayern|bb"
                + "|bbc|bbt|bbva|bcg|bcn|bd|be|beats|beauty|beer|bentley|berlin|best|bestbuy|bet|bf|bg|bh"
                + "|bharti|bi|bible|bid|bike|bing|bingo|bio|biz|bj|black|blackfriday|blanco|blockbuster|blog"
                + "|bloomberg|blue|bm|bms|bmw|bn|bnl|bnpparibas|bo|boats|boehringer|bofa|bom|bond|boo|book"
                + "|booking|boots|bosch|bostik|boston|bot|boutique|box|br|bradesco|bridgestone|broadway"
                + "|broker|brother|brussels|bs|bt|budapest|bugatti|build|builders|business|buy|buzz|bv|bw|by"
                + "|bz|bzh|ca|cab|cafe|cal|call|calvinklein|cam|camera|camp|cancerresearch|canon|capetown"
                + "|capital|capitalone|car|caravan|cards|care|career|careers|cars|cartier|casa|case|caseih"
                + "|cash|casino|cat|catering|catholic|cba|cbn|cbre|cbs|cc|cd|ceb|center|ceo|cern|cf|cfa|cfd"
                + "|cg|ch|chanel|channel|chase|chat|cheap|chintai|chloe|christmas|chrome|chrysler|church|ci"
                + "|cipriani|circle|cisco|citadel|citi|citic|city|cityeats|ck|cl|claims|cleaning|click"
                + "|clinic|clinique|clothing|cloud|club|clubmed|cm|cn|co|coach|codes|coffee|college|cologne"
                + "|com|comcast|commbank|community|company|compare|computer|comsec|condos|construction"
                + "|consulting|contact|contractors|cooking|cookingchannel|cool|coop|corsica|country|coupon"
                + "|coupons|courses|cr|credit|creditcard|creditunion|cricket|crown|crs|cruise|cruises|csc|cu"
                + "|cuisinella|cv|cw|cx|cy|cymru|cyou|cz|dabur|dad|dance|data|date|dating|datsun|day|dclk|dds"
                + "|de|deal|dealer|deals|degree|delivery|dell|deloitte|delta|democrat|dental|dentist|desi"
                + "|design|dev|dhl|diamonds|diet|digital|direct|directory|discount|discover|dish|diy|dj|dk|dm"
                + "|dnp|do|docs|doctor|dodge|dog|doha|domains|dot|download|drive|dtv|dubai|duck|dunlop|duns"
                + "|dupont|durban|dvag|dvr|dz|earth|eat|ec|eco|edeka|edu|education|ee|eg|email|emerck|energy"
                + "|engineer|engineering|enterprises|epost|epson|equipment|er|ericsson|erni|es|esq|estate"
                + "|esurance|et|eu|eurovision|eus|events|everbank|exchange|expert|exposed|express|extraspace"
                + "|fage|fail|fairwinds|faith|family|fan|fans|farm|farmers|fashion|fast|fedex|feedback"
                + "|ferrari|ferrero|fi|fiat|fidelity|fido|film|final|finance|financial|fire|firestone"
                + "|firmdale|fish|fishing|fit|fitness|fj|fk|flickr|flights|flir|florist|flowers|fly|fm|fo|foo"
                + "|food|foodnetwork|football|ford|forex|forsale|forum|foundation|fox|fr|free|fresenius|frl"
                + "|frogans|frontdoor|frontier|ftr|fujitsu|fujixerox|fun|fund|furniture|futbol|fyi|ga|gal"
                + "|gallery|gallo|gallup|game|games|gap|garden|gb|gbiz|gd|gdn|ge|gea|gent|genting|george|gf"
                + "|gg|ggee|gh|gi|gift|gifts|gives|giving|gl|glade|glass|gle|global|globo|gm|gmail|gmbh|gmo"
                + "|gmx|gn|godaddy|gold|goldpoint|golf|goo|goodhands|goodyear|goog|google|gop|got|gov|gp|gq"
                + "|gr|grainger|graphics|gratis|green|gripe|group|gs|gt|gu|guardian|gucci|guge|guide|guitars"
                + "|guru|gw|gy|hair|hamburg|hangout|haus|hbo|hdfc|hdfcbank|health|healthcare|help|helsinki"
                + "|here|hermes|hgtv|hiphop|hisamitsu|hitachi|hiv|hk|hkt|hm|hn|hockey|holdings|holiday"
                + "|homedepot|homegoods|homes|homesense|honda|honeywell|horse|hospital|host|hosting|hot"
                + "|hoteles|hotmail|house|how|hr|hsbc|ht|htc|hu|hughes|hyatt|hyundai|ibm|icbc|ice|icu|id|ie"
                + "|ieee|ifm|ikano|il|im|imamat|imdb|immo|immobilien|in|industries|infiniti|info|ing|ink"
                + "|institute|insurance|insure|int|intel|international|intuit|investments|io|ipiranga|iq|ir"
                + "|irish|is|iselect|ismaili|ist|istanbul|it|itau|itv|iveco|iwc|jaguar|java|jcb|jcp|je|jeep"
                + "|jetzt|jewelry|jio|jlc|jll|jm|jmp|jnj|jo|jobs|joburg|jot|joy|jp|jpmorgan|jprs|juegos"
                + "|juniper|kaufen|kddi|ke|kerryhotels|kerrylogistics|kerryproperties|kfh|kg|kh|ki|kia|kim"
                + "|kinder|kindle|kitchen|kiwi|km|kn|koeln|komatsu|kosher|kp|kpmg|kpn|kr|krd|kred|kuokgroup"
                + "|kw|ky|kyoto|kz|la|lacaixa|ladbrokes|lamborghini|lamer|lancaster|lancia|lancome|land"
                + "|landrover|lanxess|lasalle|lat|latino|latrobe|law|lawyer|lb|lc|lds|lease|leclerc|lefrak"
                + "|legal|lego|lexus|lgbt|li|liaison|lidl|life|lifeinsurance|lifestyle|lighting|like|lilly"
                + "|limited|limo|lincoln|linde|link|lipsy|live|living|lixil|lk|loan|loans|locker|locus|loft"
                + "|lol|london|lotte|lotto|love|lpl|lplfinancial|lr|ls|lt|ltd|ltda|lu|lundbeck|lupin|luxe"
                + "|luxury|lv|ly|ma|macys|madrid|maif|maison|makeup|man|management|mango|market|marketing"
                + "|markets|marriott|marshalls|maserati|mattel|mba|mc|mcd|mcdonalds|mckinsey|md|me|med|media"
                + "|meet|melbourne|meme|memorial|men|menu|meo|metlife|mg|mh|miami|microsoft|mil|mini|mint|mit"
                + "|mitsubishi|mk|ml|mlb|mls|mm|mma|mn|mo|mobi|mobile|mobily|moda|moe|moi|mom|monash|money"
                + "|monster|montblanc|mopar|mormon|mortgage|moscow|moto|motorcycles|mov|movie|movistar|mp|mq"
                + "|mr|ms|msd|mt|mtn|mtpc|mtr|mu|museum|mutual|mv|mw|mx|my|mz|na|nab|nadex|nagoya|name"
                + "|nationwide|natura|navy|nba|nc|ne|nec|net|netbank|netflix|network|neustar|new|newholland"
                + "|news|next|nextdirect|nexus|nf|nfl|ng|ngo|nhk|ni|nico|nike|nikon|ninja|nissan|nissay|nl|no"
                + "|nokia|northwesternmutual|norton|now|nowruz|nowtv|np|nr|nra|nrw|ntt|nu|nyc|nz|obi|observer"
                + "|off|office|okinawa|olayan|olayangroup|oldnavy|ollo|om|omega|one|ong|onl|online|onyourside"
                + "|ooo|open|oracle|orange|org|organic|orientexpress|origins|osaka|otsuka|ott|ovh|pa|page"
                + "|pamperedchef|panasonic|panerai|paris|pars|partners|parts|party|passagens|pay|pccw|pe|pet"
                + "|pf|pfizer|pg|ph|pharmacy|philips|phone|photo|photography|photos|physio|piaget|pics|pictet"
                + "|pictures|pid|pin|ping|pink|pioneer|pizza|pk|pl|place|play|playstation|plumbing|plus|pm|pn"
                + "|pnc|pohl|poker|politie|porn|post|pr|pramerica|praxi|press|prime|pro|prod|productions|prof"
                + "|progressive|promo|properties|property|protection|pru|prudential|ps|pt|pub|pw|pwc|py|qa"
                + "|qpon|quebec|quest|qvc|racing|radio|raid|re|read|realestate|realtor|realty|recipes|red"
                + "|redstone|redumbrella|rehab|reise|reisen|reit|reliance|ren|rent|rentals|repair|report"
                + "|republican|rest|restaurant|review|reviews|rexroth|rich|richardli|ricoh|rightathome|ril"
                + "|rio|rip|rmit|ro|rocher|rocks|rodeo|rogers|room|rs|rsvp|ru|ruhr|run|rw|rwe|ryukyu|sa"
                + "|saarland|safe|safety|sakura|sale|salon|samsclub|samsung|sandvik|sandvikcoromant|sanofi"
                + "|sap|sapo|sarl|sas|save|saxo|sb|sbi|sbs|sc|sca|scb|schaeffler|schmidt|scholarships|school"
                + "|schule|schwarz|science|scjohnson|scor|scot|sd|se|seat|secure|security|seek|select|sener"
                + "|services|ses|seven|sew|sex|sexy|sfr|sg|sh|shangrila|sharp|shaw|shell|shia|shiksha|shoes"
                + "|shop|shopping|shouji|show|showtime|shriram|si|silk|sina|singles|site|sj|sk|ski|skin|sky"
                + "|skype|sl|sling|sm|smart|smile|sn|sncf|so|soccer|social|softbank|software|sohu|solar"
                + "|solutions|song|sony|soy|space|spiegel|spot|spreadbetting|sr|srl|srt|st|stada|staples|star"
                + "|starhub|statebank|statefarm|statoil|stc|stcgroup|stockholm|storage|store|stream|studio"
                + "|study|style|su|sucks|supplies|supply|support|surf|surgery|suzuki|sv|swatch|swiftcover"
                + "|swiss|sx|sy|sydney|symantec|systems|sz|tab|taipei|talk|taobao|target|tatamotors|tatar"
                + "|tattoo|tax|taxi|tc|tci|td|tdk|team|tech|technology|tel|telecity|telefonica|temasek|tennis"
                + "|teva|tf|tg|th|thd|theater|theatre|tiaa|tickets|tienda|tiffany|tips|tires|tirol|tj|tjmaxx"
                + "|tjx|tk|tkmaxx|tl|tm|tmall|tn|to|today|tokyo|tools|top|toray|toshiba|total|tours|town"
                + "|toyota|toys|tr|trade|trading|training|travel|travelchannel|travelers|travelersinsurance"
                + "|trust|trv|tt|tube|tui|tunes|tushu|tv|tvs|tw|tz|ua|ubank|ubs|uconnect|ug|uk|unicom"
                + "|university|uno|uol|ups|us|uy|uz|va|vacations|vana|vanguard|vc|ve|vegas|ventures|verisign"
                + "|versicherung|vet|vg|vi|viajes|video|vig|viking|villas|vin|vip|virgin|visa|vision|vista"
                + "|vistaprint|viva|vivo|vlaanderen|vn|vodka|volkswagen|volvo|vote|voting|voto|voyage|vu"
                + "|vuelos|wales|walmart|walter|wang|wanggou|warman|watch|watches|weather|weatherchannel"
                + "|webcam|weber|website|wed|wedding|weibo|weir|wf|whoswho|wien|wiki|williamhill|win|windows"
                + "|wine|winners|wme|wolterskluwer|woodside|work|works|world|wow|ws|wtc|wtf|xbox|xerox"
                + "|xfinity|xihuan|xin|xn--11b4c3d|xn--1ck2e1b|xn--1qqw23a|xn--30rr7y|xn--3bst00m"
                + "|xn--3ds443g|xn--3e0b707e|xn--3oq18vl8pn36a|xn--3pxu8k|xn--42c2d9a|xn--45brj9c"
                + "|xn--45q11c|xn--4gbrim|xn--54b7fta0cc|xn--55qw42g|xn--55qx5d|xn--5su34j936bgsg"
                + "|xn--5tzm5g|xn--6frz82g|xn--6qq986b3xl|xn--80adxhks|xn--80ao21a|xn--80aqecdr1a"
                + "|xn--80asehdb|xn--80aswg|xn--8y0a063a|xn--90a3ac|xn--90ae|xn--90ais|xn--9dbq2a"
                + "|xn--9et52u|xn--9krt00a|xn--b4w605ferd|xn--bck1b9a5dre4c|xn--c1avg|xn--c2br7g"
                + "|xn--cck2b3b|xn--cg4bki|xn--clchc0ea0b2g2a9gcd|xn--czr694b|xn--czrs0t|xn--czru2d"
                + "|xn--d1acj3b|xn--d1alf|xn--e1a4c|xn--eckvdtc9d|xn--efvy88h|xn--estv75g|xn--fct429k"
                + "|xn--fhbei|xn--fiq228c5hs|xn--fiq64b|xn--fiqs8s|xn--fiqz9s|xn--fjq720a|xn--flw351e"
                + "|xn--fpcrj9c3d|xn--fzc2c9e2c|xn--fzys8d69uvgm|xn--g2xx48c|xn--gckr3f0f|xn--gecrj9c"
                + "|xn--gk3at1e|xn--h2brj9c|xn--hxt814e|xn--i1b6b1a6a2e|xn--imr513n|xn--io0a7i|xn--j1aef"
                + "|xn--j1amh|xn--j6w193g|xn--jlq61u9w7b|xn--jvr189m|xn--kcrx77d1x4a|xn--kprw13d"
                + "|xn--kpry57d|xn--kpu716f|xn--kput3i|xn--l1acc|xn--lgbbat1ad8j|xn--mgb9awbf"
                + "|xn--mgba3a3ejt|xn--mgba3a4f16a|xn--mgba7c0bbn0a|xn--mgbaam7a8h|xn--mgbab2bd"
                + "|xn--mgbayh7gpa|xn--mgbb9fbpob|xn--mgbbh1a71e|xn--mgbc0a9azcg|xn--mgbca7dzdo"
                + "|xn--mgberp4a5d4ar|xn--mgbi4ecexp|xn--mgbpl2fh|xn--mgbt3dhd|xn--mgbtx2b|xn--mgbx4cd0ab"
                + "|xn--mix891f|xn--mk1bu44c|xn--mxtq1m|xn--ngbc5azd|xn--ngbe9e0a|xn--node|xn--nqv7f"
                + "|xn--nqv7fs00ema|xn--nyqy26a|xn--o3cw4h|xn--ogbpf8fl|xn--p1acf|xn--p1ai|xn--pbt977c"
                + "|xn--pgbs0dh|xn--pssy2u|xn--q9jyb4c|xn--qcka1pmc|xn--qxam|xn--rhqv96g|xn--rovu88b"
                + "|xn--s9brj9c|xn--ses554g|xn--t60b56a|xn--tckwe|xn--tiq49xqyj|xn--unup4y"
                + "|xn--vermgensberater-ctb|xn--vermgensberatung-pwb|xn--vhquv|xn--vuq861b"
                + "|xn--w4r85el8fhu5dnra|xn--w4rs40l|xn--wgbh1c|xn--wgbl6a|xn--xhq521b|xn--xkc2al3hye2a"
                + "|xn--xkc2dl3a5ee0h|xn--y9a3aq|xn--yfro4i67o|xn--ygbi2ammx|xn--zfr164b|xperia|xxx|xyz"
                + "|yachts|yahoo|yamaxun|yandex|ye|yodobashi|yoga|yokohama|you|youtube|yt|yun|za|zappos|zara"
                + "|zero|zip|zippo|zm|zone|zuerich|zw)|(co[.]uk)");

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(final View view) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.input_title);

            final EditText input = new EditText(MainActivity.this);

            input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
            builder.setView(input);

            builder.setPositiveButton(
                R.string.action_go,
                new DialogInterface.OnClickListener() {

                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    Snackbar.make(view, R.string.loading, Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
                    String s = input.getText().toString();

                    // Pattern tlds = Pattern.compile("[.](ru|net|com|org|eus|es)|(co[.]uk)");

                    if (!tlds.matcher(s).find() || s.contains(" ")) {
                      s = s.replaceAll(" ", "+");
                      if (searchEngine.compareToIgnoreCase(("1")) == 0)
                        s = "https://duckduckgo.com/lite/?q=" + s;
                      else s = "https://www.google.com/search?q=" + s;
                    } else if (!s.toLowerCase().startsWith("http://")
                        && !s.toLowerCase().startsWith("https://")) {
                      s = "https://" + s;
                    }

                    Loader l = new Loader();
                    l.url = s;
                    l.execute();

                    InputMethodManager imm =
                        (InputMethodManager)
                            getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);
                    if (imm.isActive())
                      imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                  }
                });

            builder.setNegativeButton(
                R.string.action_cancel,
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    InputMethodManager imm =
                        (InputMethodManager)
                            getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);
                    if (imm.isActive())
                      imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    dialog.cancel();
                  }
                });

            builder.show();

            input.requestFocus();
            InputMethodManager imm =
                (InputMethodManager) getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
          }
        });
  }

  public String buildHistory() {
    String h =
        "<html><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,"
            + "initial-scale=1, maximum-scale=1, minimum-scale=1\"><script>//handle long pressvar touchStartTime;"
            + "document.addEventListener(\"touchstart\", function(){touchStartTime = new Date();}, false);"
            + "document.addEventListener(\"touchend\", function(evt){if(new Date() - touchStartTime >= 1000"
            + "&& evt.target.id != \"\") window.JSInterface.removeHistoryEntry(evt.target.id);}, false);</script>"
            + "<style>div{color:green;overflow:hidden;white-space:nowrap;text-overflow:ellipsis;}div.date{float:right;"
            + "color:#444444;}div.url{color:#444444;}hr{margin:8 -8px 8 -8px;}</style></head><body>"
            + "<button style=\"width: 100%;\" onclick=\"window.JSInterface.removeAllHistory()\">Remove all</button><hr>";
    for (int x = history.size() - 1; x >= 0; x--) {
      h =
          h
              + "<div id='"
              + history.get(x).getUrl()
              + "' onclick='window.JSInterface.loadFromHistory(this.id)'>"
              + "<div id='"
              + history.get(x).getUrl()
              + "'>"
              + history.get(x).getTitle()
              + "<div id='"
              + history.get(x).getUrl()
              + "' class='date'>"
              + history.get(x).getDate()
              + "</div><br>"
              + "<div id='"
              + history.get(x).getUrl()
              + "' class='url'>"
              + history.get(x).getUrl()
              + "</div></div></div><hr>";
    }
    h = h + "</body></html>";
    return h;
  }

  public ArrayList<historyEntry> readHistoryFromInternalStorage() {
    ArrayList<historyEntry> toReturn = new ArrayList<historyEntry>();
    FileInputStream fis;
    try {
      fis = this.openFileInput("history");
      ObjectInputStream oi = new ObjectInputStream(fis);
      toReturn = (ArrayList<historyEntry>) oi.readObject();
      oi.close();
    } catch (Exception e) {
      Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
    return toReturn;
  }

  public void saveHistoryToInternalStorage() {
    try {
      FileOutputStream fos = this.openFileOutput("history", this.MODE_PRIVATE);
      ObjectOutputStream of = new ObjectOutputStream(fos);
      of.writeObject(history);
      of.flush();
      of.close();
      fos.close();
    } catch (Exception e) {
      Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    WebView wv = (WebView) findViewById(R.id.webView);
    wv.setWebViewClient(new myWebViewClient());
    return true;
  }

  private class Loader {

    String url;
    String content;
    String title;
    long bytes = 0;

    protected Void execute() {

      // send the text request
      SmsManager smgr = SmsManager.getDefault();
      smgr.sendTextMessage(MOBILE_NUMBER, null, url.toString(), null, null);

      Log.w("SMS Sender", "Requested webpage via SMS");

      smsBroadcastReceiver.setListener(
          new SmsBroadcastReceiver.Listener() {
            @Override
            public void onTextReceived(String text) {
              if (text.equals("=====")) {
                processWebsite(this.collector.toString());
              } else {
                collector.append(text);
              }
            }
          });

      return null;
    }

    Void processWebsite(String website) {
      try {

        // start of transmission
        byte[] byteArray = Base64.decode(website, Base64.DEFAULT);

        Inflater decompressor = new Inflater();
        decompressor.setInput(byteArray);

        byte[] output = new byte[byteArray.length * 3];

        decompressor.inflate(output);

        decompressor.end();

        String htmlOut = new String(output, 0, output.length, "UTF-8");
        Log.w("html out", htmlOut);
        Document doc = Jsoup.parse(htmlOut);

        title = doc.title();

        content =
            getString(R.string.page_size)
                + ": "
                + humanReadableByteCount(doc.toString().length())
                + "<hr>";

        content = content + doc.body().toString();
        // make paths absolute
        URL u = new URL(url);
        String base = u.getProtocol() + "://" + u.getHost() + "/";
        content = content.replaceAll("href=\"/", "href=\"" + base);
        // remove images
        content = content.replaceAll("<img.*?>", "");

        // So this used to be an async function, however at some point
        // decided to make it run on the main thread. Therefore we
        // need to run this.
        this.onPostExecute(null);

      } catch (IOException e) {
        content = e.getMessage();
        Toast.makeText(MainActivity.this, content, Toast.LENGTH_LONG).show();
      } catch (DataFormatException e) {
        e.printStackTrace();
      }
      return null;
    }

    protected void onPostExecute(Void result) {
      setTitle(title);
      WebView wv = (WebView) findViewById(R.id.webView);
      if (preferences.getString("themes", "1").compareToIgnoreCase(("0")) == 0) {
        content =
            "<html><head><title>atob</title><meta charset=\"utf-8\"><meta name=\"viewport\""
                + "content=\"width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1\">"
                + "<style>html{color:#cccccc;background:#444444;}a{color:#2196F3;}</style></head>"
                + content
                + "</body></html>";
      } else {
        content =
            "<html><head><title>atob</title><meta charset=\"utf-8\"><meta name=\"viewport\""
                + "content=\"width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1\"></head><body>"
                + content
                + "</body></html>";
      }
      wv.loadDataWithBaseURL("", content, "text/html", "utf-8", "");
      history.add(new historyEntry(title, url, new Date()));
      saveHistoryToInternalStorage();
    }

    public String humanReadableByteCount(long bytes) {
      int unit = 1000;
      if (bytes < unit) return bytes + " B";
      int exp = (int) (Math.log(bytes) / Math.log(unit));
      char pre = ("kMGTPE").charAt(exp - 1);
      return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
  }

  private class myWebViewClient extends WebViewClient {
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      Snackbar.make(
              getWindow().getDecorView().getRootView(), R.string.loading, Snackbar.LENGTH_LONG)
          .setAction("Action", null)
          .show();
      Loader l = new Loader();
      l.url = url;
      l.execute();
      return true;
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      startActivity(new Intent(this, SettingsActivity.class));
      return true;
    }
    if (id == R.id.action_about) {
      startActivity(new Intent(this, AboutActivity.class));
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    Toast.makeText(this, "intent", Toast.LENGTH_LONG);
    if (intent != null) {
      WebView wv = (WebView) findViewById(R.id.webView);
      wv.loadUrl(intent.getExtras().getString("url"));
    }
  }

  public class JavaScriptInterface {
    @JavascriptInterface
    public void removeAllHistory() {
      new AlertDialog.Builder(MainActivity.this)
          .setTitle("Remove all history")
          .setMessage("Are you sure you want to remove all the history?")
          .setPositiveButton(
              "Yes",
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  history.clear();
                  saveHistoryToInternalStorage();
                  Toast.makeText(getBaseContext(), "history cleared", Toast.LENGTH_SHORT).show();
                  finish();
                  startActivity(getIntent());
                }
              })
          .setNegativeButton("No", null)
          .show();
    }

    @JavascriptInterface
    public void loadFromHistory(String u) {
      Snackbar.make(
              getWindow().getDecorView().getRootView(), R.string.loading, Snackbar.LENGTH_LONG)
          .setAction("Action", null)
          .show();
      Loader l = new Loader();
      l.url = u;
      l.execute();
    }

    @JavascriptInterface
    public void removeHistoryEntry(String u) {}
  }
}
