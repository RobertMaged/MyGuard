package com.rmsr.myguard.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Ignore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Ignore
public class TextValidateTest {
    Pattern pattern = Pattern.compile("");
    Matcher matcher = pattern.matcher("");

    String[] split;
    String apiPasswordResponse = "00487B695AFF4CCDBDAA547A5301EF5B810:2\n" +
            "    00727778DA2BBDA429A2F4CB3546D4E804F:5\n" +
            "    00757D1248940F19DD74FAA1EA3EF3B5607:4\n" +
            "    013A7252D3C38F3C0DF9ADD2F430E669C4A:1\n" +
            "    01435690CD862012C05BCEC042F186BC461:10\n" +
            "    01A95975222F19677AE12E8088CDEB72CCC:9\n" +
            "    01F6F81A918AADB0DBA4B65BDFBFBFED585:3\n" +
            "    027B2F08F4741774A28D4A9A4598194B3EB:3\n" +
            "    02CE8572EA146870346904C0FEFCA411DEF:2\n" +
            "    03FF97F6EF05FC981BFDDCEFCA89ACE0270:2\n" +
            "    04754C3B30EAE153A5D22A749EDED5CB967:1\n" +
            "    04A9DB85ACBB47B7AD9AB48147C42223028:2\n" +
            "    04B311BA7D0E6C3BEBE360897D2CB555917:1\n" +
            "    04B5E6D7B9ED2ABA270E32920D0DE7F4C93:1\n" +
            "    050512FF9C75873685D175BE5E939225D7E:54\n" +
            "    0516B5EEF7F6CDF206716558A86F345ABAE:1\n" +
            "    06494C57D3B4ADBDBEBDA478D47DC71CD6A:2\n" +
            "    06BACB490A8B8358432AD202D6D8C461ED6:1\n" +
            "    06C7D33CBC4388641DBCE1C3F697F7110F0:3\n" +
            "    072D6EBE0D343A380932A8A1B5BB63EC344:2\n" +
            "    0756A87B465BDC0635030DA60E1E5A77D85:3\n" +
            "    08F473DC709BD7AC81AA8D5EB688FC57619:1\n" +
            "    0AA13FDFCA803DC6E8FAF13A2E98DCAA890:3\n" +
            "    0AA84306AF5FD2CC0979450FE29149589D3:3\n" +
            "    0AE34C5A0A229D1898D75808C1C5B470ACD:3\n" +
            "    0B6C62B1DC6A86C7F28DED63EE65BEDC53F:1\n" +
            "    0C196D10655DFC159F6F220533B6895E5C9:4\n" +
            "    0C8C0E58254B9C53C48878FBCFD8AF8FEBB:2\n" +
            "    0C97CCBE681CB0FEE631DE8688F0B4D3F53:1\n" +
            "    0E819CC693EDD5264313F5FE492F7F6D508:1\n" +
            "    0EC6096DDD4A3370C8F682C0E287C861374:2\n" +
            "    0EF1ABD56D5045DF56121BF7294935BB151:2\n" +
            "    0FA578F95CE38C63494701CA09B34A0EB73:4\n" +
            "    0FDFEC9A6178B8999F3348D384443357208:12\n" +
            "    101B8C4FDF045604D2EE2BDA88DABFEFF00:6\n" +
            "    104FA4704AE7680C82D99D8B440B5DA4D85:1\n" +
            "    10F95A3F47EFDB86C157DBCC03B1577BFBE:1\n" +
            "    10FA2E0F4C4F9367A7BC1D7092B042CD825:3\n" +
            "    1126B5947B04E6CE389E09EF084E4E4F77D:10\n" +
            "    1131AD29A398D818DD9365A3961C21DB9CF:2\n" +
            "    11C21495B4951EC130A9CBEA20CA83C069B:1\n" +
            "    11F5AED9B2D4302339209D4772863B767C4:5\n" +
            "    131C2836AE502F61F478D8DF550F40BA1DC:1\n" +
            "    136CBA88004334773F0AF4054A152F7F3CF:7\n" +
            "    13A1B6AAE3B89D174F8528EB8E936E7F676:1\n" +
            "    13A2B586B516A7A5903D5D8BCC61C5EC4EE:4\n" +
            "    1420DFF47402D1780659707C526BC92A1B7:1\n" +
            "    1498C1F9C72A7CC544A1246A29918115E78:1\n" +
            "    14BE6ADC1EBB6664F87FD532A0144202FBC:5\n" +
            "    152E99C5A0FBBC124745A5419185404D948:1\n" +
            "    158615669D91AAF28F54241D3B47F7F5BF4:1\n" +
            "    16327693899809176A36428432AC4563E86:10\n" +
            "    16387AB445CB1DC663E2474939B170A577A:3\n" +
            "    16E50D18579EB2F0EA0446EBA59F4FF305A:1\n" +
            "    17387021E2EF6CFFDB503864597EA3F729B:1\n" +
            "    174978494307597C58FD345E190F92A87D0:1\n" +
            "    179FDFA9B542AB5CE8F9DD6DA3129E89AFF:3\n" +
            "    18ACBEF6334681AA69918D05C4E1FD3D3AB:1\n" +
            "    18F6FD2CBED788C03528557BFE2A1710004:1\n" +
            "    18FCEE46BDDF61FA06F80A7B3B87A910134:1\n" +
            "    19D2E0D2704C0ACAFD34F9EFC39CC448DB0:4\n" +
            "    1A5CC4E2F7624DE1A3A4F7486B0C97F0C39:5\n" +
            "    1B30BED574F1C6C6E1E3F905AC354E5AE23:3\n" +
            "    1B4E7BB55533C0E2A339A412E492810D72B:2\n" +
            "    1B87D3D5ACEEF9DA4F0337476652B8FA66F:4\n" +
            "    1B9F660C5BEC0368B423AA1F6D32FC008D7:2\n" +
            "    1BB71FF74B2AA4F8771E3A566D4797A3CD0:2\n" +
            "    1BCE06B4A75217F6D9991487D9F3353FEE7:2\n" +
            "    1BD1E6B6330FD1253FA56362560186DB24B:2\n" +
            "    1C08D8EAE442B2C4D69573381254EF797BB:1\n" +
            "    1C6BB66159DB22A698C44B0918276A5D877:10\n" +
            "    1C7A727E9927D6009B8F4E4E9BE1C44AB65:3\n" +
            "    1C881E3C0097F874DA758A725243FBD3671:1\n" +
            "    1CAB45541F0286016B7431EF6EAEB379B2E:1\n" +
            "    1CBB253ED831ABD5620E59A818A88E3CAC6:1\n" +
            "    1CD04E6DEA6007CFE642E849235673CB558:4\n" +
            "    1D2862BEB6AECE41DC3F33223B7D0491542:2\n" +
            "    1DCDB9902B4027C4143CE5FFB18E0F5E2A2:2\n" +
            "    1DEBA32FEDDDC72261FFEFE401A7ADFE507:2\n" +
            "    1E7BD370FC3D51848FEF00DCFBDDC35653E:6\n" +
            "    1FA7DCB16FA32E1C4EAD6FAE8747E53A982:3\n" +
            "    1FF29706ECF9AE4883CB0275C14A5D00113:1\n" +
            "    21209DE0BE33CD3C21EBEE60FE0D4FFC83A:1\n" +
            "    2175AF7C6869EBA8F2F98BEED55F79BDE02:5\n" +
            "    21D1BF93E103C8E0DD9E04517860975894C:10\n" +
            "    220BAA5405993FFD848EE15E8EA52A5761E:6\n" +
            "    229CEFA52C800E98B8CCCA41953C56FBB3C:1\n" +
            "    22B685A222C1BD2315E8715369B033A3B9F:3\n" +
            "    22CFC36FF11691CC2A526E148DEA70CE37F:3\n" +
            "    2354BA41F452A4371411CBCC3E60B150896:2\n" +
            "    239F6F28F3D251A59488B3A4A64CF96C06D:2\n" +
            "    23C0D41B7A2B75FA3466504FABFCE45E538:1\n" +
            "    241963B7DEA9700C383C9357FD0BAF0A3E6:100\n" +
            "    246F7D3ED75051908DD14D88A56BFC4F1BC:4\n" +
            "    2473AE295BBBFE67EA05A5BDAC10118E675:1\n" +
            "    24F1A712821909C811D3F646D9EF5B6E343:4\n" +
            "    2579B5825EAC24BD98110EE31B88FF9F0DD:15\n" +
            "    2764A1BBE99BC7180CF2DFCC09E592D1008:6\n" +
            "    27F54F6182E90634AC0E750E5B2793D79A5:1\n" +
            "    28654ED18D4AED364AA0E4900EDFF076726:4\n" +
            "    28C4E28AF99B0BF053A927C795071674BA7:1\n" +
            "    28CA33A7A97A650084FEF009E5E29A93591:2\n" +
            "    29E79FBE5F2557DCBA8E470D7864FA6C149:2\n" +
            "    29EAEF7\n" +
            "00487B695AFF4CCDBDAA547A5301EF5B810:2\n" +
            "    00727778DA2BBDA429A2F4CB3546D4E804F:5\n" +
            "    00757D1248940F19DD74FAA1EA3EF3B5607:4\n" +
            "    013A7252D3C38F3C0DF9ADD2F430E669C4A:1\n" +
            "    01435690CD862012C05BCEC042F186BC461:10\n" +
            "    01A95975222F19677AE12E8088CDEB72CCC:9\n" +
            "    01F6F81A918AADB0DBA4B65BDFBFBFED585:3\n" +
            "    027B2F08F4741774A28D4A9A4598194B3EB:3\n" +
            "    02CE8572EA146870346904C0FEFCA411DEF:2\n" +
            "    03FF97F6EF05FC981BFDDCEFCA89ACE0270:2\n" +
            "    04754C3B30EAE153A5D22A749EDED5CB967:1\n" +
            "    04A9DB85ACBB47B7AD9AB48147C42223028:2\n" +
            "    04B311BA7D0E6C3BEBE360897D2CB555917:1\n" +
            "    04B5E6D7B9ED2ABA270E32920D0DE7F4C93:1\n" +
            "    050512FF9C75873685D175BE5E939225D7E:54\n" +
            "    0516B5EEF7F6CDF206716558A86F345ABAE:1\n" +
            "    06494C57D3B4ADBDBEBDA478D47DC71CD6A:2\n" +
            "    06BACB490A8B8358432AD202D6D8C461ED6:1\n" +
            "    06C7D33CBC4388641DBCE1C3F697F7110F0:3\n" +
            "    072D6EBE0D343A380932A8A1B5BB63EC344:2\n" +
            "    0756A87B465BDC0635030DA60E1E5A77D85:3\n" +
            "    08F473DC709BD7AC81AA8D5EB688FC57619:1\n" +
            "    0AA13FDFCA803DC6E8FAF13A2E98DCAA890:3\n" +
            "    0AA84306AF5FD2CC0979450FE29149589D3:3\n" +
            "    0AE34C5A0A229D1898D75808C1C5B470ACD:3\n" +
            "    0B6C62B1DC6A86C7F28DED63EE65BEDC53F:1\n" +
            "    0C196D10655DFC159F6F220533B6895E5C9:4\n" +
            "    0C8C0E58254B9C53C48878FBCFD8AF8FEBB:2\n" +
            "    0C97CCBE681CB0FEE631DE8688F0B4D3F53:1\n" +
            "    0E819CC693EDD5264313F5FE492F7F6D508:1\n" +
            "    0EC6096DDD4A3370C8F682C0E287C861374:2\n" +
            "    0EF1ABD56D5045DF56121BF7294935BB151:2\n" +
            "    0FA578F95CE38C63494701CA09B34A0EB73:4\n" +
            "    0FDFEC9A6178B8999F3348D384443357208:12\n" +
            "    101B8C4FDF045604D2EE2BDA88DABFEFF00:6\n" +
            "    104FA4704AE7680C82D99D8B440B5DA4D85:1\n" +
            "    10F95A3F47EFDB86C157DBCC03B1577BFBE:1\n" +
            "    10FA2E0F4C4F9367A7BC1D7092B042CD825:3\n" +
            "    1126B5947B04E6CE389E09EF084E4E4F77D:10\n" +
            "    1131AD29A398D818DD9365A3961C21DB9CF:2\n" +
            "    11C21495B4951EC130A9CBEA20CA83C069B:1\n" +
            "    11F5AED9B2D4302339209D4772863B767C4:5\n" +
            "    131C2836AE502F61F478D8DF550F40BA1DC:1\n" +
            "    136CBA88004334773F0AF4054A152F7F3CF:7\n" +
            "    13A1B6AAE3B89D174F8528EB8E936E7F676:1\n" +
            "    13A2B586B516A7A5903D5D8BCC61C5EC4EE:4\n" +
            "    1420DFF47402D1780659707C526BC92A1B7:1\n" +
            "    1498C1F9C72A7CC544A1246A29918115E78:1\n" +
            "    14BE6ADC1EBB6664F87FD532A0144202FBC:5\n" +
            "    152E99C5A0FBBC124745A5419185404D948:1\n" +
            "    158615669D91AAF28F54241D3B47F7F5BF4:1\n" +
            "    16327693899809176A36428432AC4563E86:10\n" +
            "    16387AB445CB1DC663E2474939B170A577A:3\n" +
            "    16E50D18579EB2F0EA0446EBA59F4FF305A:1\n" +
            "    17387021E2EF6CFFDB503864597EA3F729B:1\n" +
            "    174978494307597C58FD345E190F92A87D0:1\n" +
            "    179FDFA9B542AB5CE8F9DD6DA3129E89AFF:3\n" +
            "    18ACBEF6334681AA69918D05C4E1FD3D3AB:1\n" +
            "    18F6FD2CBED788C03528557BFE2A1710004:1\n" +
            "    18FCEE46BDDF61FA06F80A7B3B87A910134:1\n" +
            "    19D2E0D2704C0ACAFD34F9EFC39CC448DB0:4\n" +
            "    1A5CC4E2F7624DE1A3A4F7486B0C97F0C39:5\n" +
            "    1B30BED574F1C6C6E1E3F905AC354E5AE23:3\n" +
            "    1B4E7BB55533C0E2A339A412E492810D72B:2\n" +
            "    1B87D3D5ACEEF9DA4F0337476652B8FA66F:4\n" +
            "    1B9F660C5BEC0368B423AA1F6D32FC008D7:2\n" +
            "    1BB71FF74B2AA4F8771E3A566D4797A3CD0:2\n" +
            "    1BCE06B4A75217F6D9991487D9F3353FEE7:2\n" +
            "    1BD1E6B6330FD1253FA56362560186DB24B:2\n" +
            "    1C08D8EAE442B2C4D69573381254EF797BB:1\n" +
            "    1C6BB66159DB22A698C44B0918276A5D877:10\n" +
            "    1C7A727E9927D6009B8F4E4E9BE1C44AB65:3\n" +
            "    1C881E3C0097F874DA758A725243FBD3671:1\n" +
            "    1CAB45541F0286016B7431EF6EAEB379B2E:1\n" +
            "    1CBB253ED831ABD5620E59A818A88E3CAC6:1\n" +
            "    1CD04E6DEA6007CFE642E849235673CB558:4\n" +
            "    1D2862BEB6AECE41DC3F33223B7D0491542:2\n" +
            "    1DCDB9902B4027C4143CE5FFB18E0F5E2A2:2\n" +
            "    1DEBA32FEDDDC72261FFEFE401A7ADFE507:2\n" +
            "    1E7BD370FC3D51848FEF00DCFBDDC35653E:6\n" +
            "    1FA7DCB16FA32E1C4EAD6FAE8747E53A982:3\n" +
            "    1FF29706ECF9AE4883CB0275C14A5D00113:1\n" +
            "    21209DE0BE33CD3C21EBEE60FE0D4FFC83A:1\n" +
            "    2175AF7C6869EBA8F2F98BEED55F79BDE02:5\n" +
            "    21D1BF93E103C8E0DD9E04517860975894C:10\n" +
            "    220BAA5405993FFD848EE15E8EA52A5761E:6\n" +
            "    229CEFA52C800E98B8CCCA41953C56FBB3C:1\n" +
            "    22B685A222C1BD2315E8715369B033A3B9F:3\n" +
            "    22CFC36FF11691CC2A526E148DEA70CE37F:3\n" +
            "    2354BA41F452A4371411CBCC3E60B150896:2\n" +
            "    239F6F28F3D251A59488B3A4A64CF96C06D:2\n" +
            "    23C0D41B7A2B75FA3466504FABFCE45E538:1\n" +
            "    241963B7DEA9700C383C9357FD0BAF0A3E6:100\n" +
            "    246F7D3ED75051908DD14D88A56BFC4F1BC:4\n" +
            "    2473AE295BBBFE67EA05A5BDAC10118E675:1\n" +
            "    24F1A712821909C811D3F646D9EF5B6E343:4\n" +
            "    2579B5825EAC24BD98110EE31B88FF9F0DD:15\n" +
            "    2764A1BBE99BC7180CF2DFCC09E592D1008:6\n" +
            "    27F54F6182E90634AC0E750E5B2793D79A5:1\n" +
            "    28654ED18D4AED364AA0E4900EDFF076726:4\n" +
            "    28C4E28AF99B0BF053A927C795071674BA7:1\n" +
            "    28CA33A7A97A650084FEF009E5E29A93591:2\n" +
            "    29E79FBE5F2557DCBA8E470D7864FA6C149:2\n" +
            "    29EAEF7";

    private static void log(String msg) {
        System.out.println(System.currentTimeMillis() + ": " + Thread.currentThread().getName() + ": " + msg);
    }

    private static void threadSleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getPasswordsTest(String password) {

        String first5Hash = password.substring(0, 5);
        String first5 = password.substring(0, 5);
        String restOfHash = password.substring(5);
        //Log.d(TAG, "getPasswords: First 5 is: " + first5Hash);
        return restOfHash;
    }

    public void timeStamp() {
        final boolean[] connection = {false};
//       MutableLiveData<Boolean> connection = new MutableLiveData<>(false);

        Observable.range(0, 10)
                .subscribeOn(Schedulers.io())
                .map(it -> {
                            threadSleep(2000);
                            connection[0] = !connection[0];
                            return connection;
                        }
                ).subscribe(next -> log("Connection state= " + next[0]));

        Observable.just(connection)
                .subscribeOn(Schedulers.io())
                .subscribe(next -> log("Observe connection=:" + next[0]));


//        MyLog.d("Start");
//        Observable.just(1, 2, 3)
//                .observeOn(Schedulers.io())
//                .delay(1, TimeUnit.SECONDS)
//                .doOnSubscribe(disposable -> MyLog.d("Subscribed"))
//               // .delaySubscription(3, TimeUnit.SECONDS, Schedulers.io())
//                .subscribe(success -> MyLog.d(success+""), error -> MyLog.d(error.getMessage()));

        threadSleep(15000);
    }

    //    @Test
    public void getPasswordsTest() {
        //getPasswordsTest("12325801232580");


//        Map<String, String> map = Arrays.stream(api.split("\\n"))
//                .map(s -> s.split(":"))
//                .collect(Collectors.toMap(new Function<String[], String>() {
//                    @Override
//                    public String apply(String[] s) {
//                        return s[0].trim();
//                    }
//                }, new Function<String[], String>() {
//                    @Override
//                    public String apply(String[] s) {
//                        if (s.length > 1)
//                            return s[1].trim();
//                        else
//                            return "0";
//                    }
//                }));

        Map<String, String> map = new HashMap<String, String>();
        String[] lineSplit = apiPasswordResponse.split("\\n");

        for (String s : lineSplit) {
            String[] colonSplit = s.split(":");

            String hash = colonSplit[0].trim();

            String number = "0";
            if (colonSplit.length > 1) {
                number = colonSplit[1].trim();
            }

            map.put(hash, number);
        }

        String t = map.get("28654ED18D4AED364AA0E4900EDFF076726");

        assertEquals("4", map.get("28654ED18D4AED364AA0E4900EDFF076726"));
    }

    //    @Test
    public void validPhoneTest() {
        class MyPhone {
            public String number;
            public boolean isValid;

            public MyPhone(String number, boolean isValid) {
                this.number = number;
                this.isValid = isValid;
            }
        }

        List<MyPhone> phones = new ArrayList<>();
        phones.add(new MyPhone("01144206746", false));
        phones.add(new MyPhone("201144206746", true));
        phones.add(new MyPhone("30144206746", false));
        phones.add(new MyPhone("20114420676", false));
        phones.add(new MyPhone("01244206746", false));
        phones.add(new MyPhone("201244206746", true));
        phones.add(new MyPhone("01044206746", false));
        phones.add(new MyPhone("301144206746", false));
        phones.add(new MyPhone("01598753987", false));
        phones.add(new MyPhone("201598753987", true));
        phones.add(new MyPhone("201698753987", false));
        phones.add(new MyPhone("001698753987", false));

        Map<String, Boolean> phones2 = new HashMap<>();
        phones2.putIfAbsent("01144206746", false);
        phones2.putIfAbsent("201144206746", true);
        phones2.putIfAbsent("30144206746", false);
        phones2.putIfAbsent("20114420676", false);
        phones2.putIfAbsent("01244206746", false);

        Stack<Boolean> result = new Stack<>();
        Stack<Boolean> expected = new Stack<>();
        for (MyPhone phone : phones) {
//            result.add(TextValidate.isValidPhoneNumber(phone.number));
            expected.add(phone.isValid);
            //assertEquals(phone.isValid, validResult);;
        }


        assertArrayEquals(expected.toArray(), result.toArray());
    }

    //    @Test
    public void validEGPhoneTest() {
        class MyPhone {
            public String number;
            public boolean isValid;

            public MyPhone(String number, boolean isValid) {
                this.number = number;
                this.isValid = isValid;
            }
        }

        List<MyPhone> phones = new ArrayList<>();
        phones.add(new MyPhone("01144206746", true));
        phones.add(new MyPhone("201144206746", false));
        phones.add(new MyPhone("30144206746", false));
        phones.add(new MyPhone("20114420676", false));
        phones.add(new MyPhone("01244206746", true));
        phones.add(new MyPhone("201244206746", false));
        phones.add(new MyPhone("01044206746", true));
        phones.add(new MyPhone("301144206746", false));
        phones.add(new MyPhone("01598753987", true));
        phones.add(new MyPhone("201598753987", false));
        phones.add(new MyPhone("201698753987", false));
        phones.add(new MyPhone("01698753987", false));
        phones.add(new MyPhone("01098753987", true));
        phones.add(new MyPhone("01098753987", true));
        phones.add(new MyPhone("0109875397", false));
        phones.add(new MyPhone("0149875397", false));


        Stack<Boolean> result = new Stack<>();
        Stack<Boolean> expected = new Stack<>();
        for (MyPhone phone : phones) {
//            result.add(TextValidate.isValidEGPhoneNumber(phone.number));
            expected.add(phone.isValid);
            //assertEquals(phone.isValid, validResult);;
        }


        assertArrayEquals(expected.toArray(), result.toArray());
    }

    //    @Test
    public void validEmailTest() {

        List<String> mailT = new ArrayList<>();
        mailT.add("rmsr888@gmail.com");
        mailT.add("sla,d;alsd,");
        mailT.add("ssjdfn@dkmfdlkf");
        mailT.add("mofjfks/ds@gmail.com");
        mailT.add("rrnsmrw@yahoo.om");

        for (String item : mailT) {
            if (validMail(item))
                System.out.println(item + " , is valid");
            else
                System.out.println(item + " , not valid");
        }

        //assertEquals("4",);
    }

    private boolean validMail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}