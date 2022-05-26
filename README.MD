# NTLM Crack

Java program intended for efficiently breaking NTLM hashes. Built with JDK 17.

## Description

The program accepts input in the form of files, the path of which should be marked using the flags described below.
The necessary inputs are:
- List of NTLM hashes, seperated by newlines.
- Wordlists for breaking the hashes.
**OR**
- Pre-generated or a custom table. NB **When providing a table you need to specifiy the delimiter**

> Provided files should be formatted in a way that every word/hash/pair is on a seperate line. Ex HASH1\nHASH2\nHASH3\nHASH4\n etc..

When a wordlist is provided, a table in the form of word:hash is generated. The table generation process is realized using the external merge sort algorithm, due to the fact that the provided wordlist may not fit into memory. Memory-allocation is done automatically. Due to the use of the external merging the process creates temporary files that consists of sorted pairs of words and hashes that are later combined into a larger table with K-way-merging. The merging takes place after all of the smaller files have been generated. **This should be a one-time process**, meaning that when you have already created a table using a specified wordlist, simply provide the table not the wordlist when running it again.

Regardless of whether the table was generated or provided, its handled the same. When starting the hash cracking process the program reads a specified amount of rows( default is 90000, but can be changed using the flags) into memory and uses binary search to determine whether any provided hashes match any of the hashes in memory. This is then repeated until the table is exhausted.

### Dependencies

The project is compiled into a .jar file, when running that the only requirement is Java.
Otherwise the program utilizes a couple of libraries using Maven.

### Installing & running

- Get the JDK
- Install Maven
- mvn install - builds jar (requires signing)

OR

Simply run the .jar file in "target".

### Executing program

```
java -jar PATH_TO\NtlmCrack.jar

FLAGS:

-s or --source: 	 Source for a list of hashes
-w or --wordlist: 	 Source for a wordlist
-t or --table: 	         Source for a custom/pre-generated table
-dl or --delimiter: 	 Separator for words and hashes in provided table (DEFAULT = "::")
-d or --destination: 	 Destination for the generated table (DEFAULT = ".\")
-n or --name:            Name for the generated table (DEFAULT = "genTable.txt")
-o or --outname: 	 Name for the file that stores cracked hashes
-mt or --maxtemp: 	 Amount of allowed temporary files (DEFAULT = 1024)
-mb or --maxblock:       Amount of rows read into memory when breaking hashes (DEFAULT = 90 000)
-h or --help: 	         Display the list of flags.

Ex.
java -jar .\NtlmCrack.jar -s "hashes.txt" -w "hashkiller.txt"
java -jar .\NtlmCrack.jar -s "hashes.txt" -t "table.txt" -dl "::"
```
## Dictionaries

Not included in this repository currently, but i'll add links to the full dictionaries when i can.

Here are a couple of excerpts as to what the dictionaries contain:

```
haldjas::00CB495840567B2235CAAECC7E0DA9B4
pasimaailm::00CBBBDC9D07CE359EBF022E6A2328EF
94zi6omx::00D385A6854D4A81BAEE80B1F9131B41
osram::00D43667BFBA34769DED470081A0B1D3
depechemode::00D5D70277DAD72DB0B9E8DAF58B97BF
virmaline::00D6B319381830EE4FFE256B4DA80F67
karinka::00D6DD90894839F2913C0C4C79D17E82
lemonade::00D875CD1BF9627A3DB7D5B8C5E50AE1
saare::00D96D9E28AFD6BB198BBBF2C3C237EB
kolp::00DB2FED29A2374A902F7C1EA7CCA062
karikakar::00DE22FE146114A047CC69840D8CB3D8
kiiktool::00E0454526DD94D60391EDC04C1A88BF
karupea::00E54478624FA6C9C1F8FA1F4F42A726
battleon::00E5B77C3A5E939D71134A94477AA66C
scream::00E6D52D43380CE2D56CA14130DF66A7
p1ll3v11r::00E84F3E44984124236803D24E18E8E2
sandraly::00E87400C5F4F2B7E24C76AD401DF25A
sunset::00ED7D2ABA310BEBDE9BE66F28D70EBE
everyday::00EE24E6187007E350BB3B2434395D83
foxfox::00EE53091AF030C01CEF60F0A95D5846
baobei::00F0E9473FE69078C228DDB70E5C41A6
baobei::00F0E9473FE69078C228DDB70E5C41A6
dirdir::00F0FD08126C23A616DD4FC4BBF9FECC
mikasakib::00F4EC173E19A5256733C28FBCF1A7D3
kitsasking::00F4F7B31246CD45456A9B2FEB6F04F4
marco::00FA5454AD511D5CFA4E65D662A93346
marco::00FA5454AD511D5CFA4E65D662A93346
akmid::00FC4442A2C453B365425248D897CD97
n1tef1ght::010203508D146D690DE83B63820F3494
lezuul::0106A8AD94A70A22E4042658D55E7AFA
shakur::01076FE615313A1D5D5278A17B3CB349
kennet::010C96B8B4DC9B7FDBADFED1EA137D3A
eestil6vi::010DAE30F6932E0F27E61492BA64674A
yfnfif::011171C21CD1C55A3D187139D8DDD852
ateras::011F9570BFCD749E5E9C11D45F8D82F7
shengli::01200455E098BC0DAB578530E244B791
mikk5mikk::012152DA01476282E045FAFFB6954C7B
6101kalla::012668DF36D3B3E0AAEA8671240A0BF3
akent::0126BB04211C99F3626B20C514C8C021
empsscape::01291A0C6E681CEFE47D71F73998DE36
irene::013109C22287B99A55E0AA332B4248CD
l4mmas::0136642FF4BCCA4DD019CC9087A58D65
angelok::0139472C4987A212E5A1F64783421F83
sometimes::013CDFCC3E7C9B52DD92ED8A7CCF460E
minemasiit::013DFEFA77141F6F368A939D334633F9
lohk::0144AB1CBD18EC6A9B0A55CAC3297DE1
maarika::0147836615DB5D444FDCFDA31ABEE04A
eterg::0147EA7F4DE4C4463791D179FF840C99
papagoi::01485D3749B0A55A60A72A904CA22F1D
marje::0149AD87064C408B2D6A92AA36BD47E4
renee::014B31D84D75D686AFC8BD3762A3D60F
raunomeister::014F34FC64A33FA6CB91F09E2640407F
viljamaa::01503DB4C1B0D3347BA7C2F452C6C856
woshiaini::015074BAF0649776C6023337C57175E9
ehanurm::0152BE674D9D1B7C341CEA321D5753D1
activision::0153BE58BD0F28A7158DCA8CD1610FBD
nabasaba::015522C69D9859DCB8558C1BAEBCF53F
midateha::01564E7D0112AE69D2683178D33E3535
haintimo::0157733817D5AAF8C725E9A682291236
rangaj::015885A08660FDA1C2E106D2012AED90
kassik::015F633173520CA45F362607ABA36C60
katalyn::016015ACBEFFB5DE5B67B788E12F55C4
tabas::01690A65FE0CAE2E58EE60090F41F3A5
metsaelukas::016A38C7121FFA0AB980870CE6296AF1
surround::016EE5DB8B9C27A2DA9BA8E7C83D1018
sektorgaza::0172DA8A88E586325D308B54A1DA24E8
mazin::017B81C7EC5BD1DC08DEB0238B3D521A
linlin::017BCF5644ABF749109681A32BC6F5AA
linlin::017BCF5644ABF749109681A32BC6F5AA
meri::017D7A243DD9E75F9114001344B6C4FB
noobahv::01856C3092AFD6ADBF06E34E2F084373
bravonus::0185D91C51B2A63E99C92D492BF5F596
paroolr2p::018A25DA82225F8B28098AAA9127BAC9
happyend::018BB6207CFA2C67D2965492324D9D24
lihakala::018C345A081764DBC80A1C4ED7D53FD0
dimochka::018CA2EAD8282BC15DE99729DAFF39BF
jonatan::0195CA130A44A53C7DD09C96487E3540
kiizuke::019821108949917A97808047AC1D2747
olustvere::01991D5E5D1832E0ADB462DE325FE10E
tronic::019A78B6A15AA406CDEC1EAECEE742D0
ruskaja::01A201D6A44CA013ACAD7FBA448AEE14
teelefm::01AC58EEBD17D8964E0D7A329F6021A2
liza::2114D6A5A040C2E66C4DDA8C3A5DA616
juunikoer::21178D2CDAABD41B2AA290BF78C3A1C1
lzx::2117BB6F39CDCF0A5454A5DC5BB6A58F
kalakesed::211CDDB47049886370C0306D63F0C9D5
enrike::211E9D92878CAC477729769D2B3A4515
moonali::212109FC5181E23375EB46CBE24EE686
reflex::21234A7FD03D43B2BE2701115F132B2A
nikats::2123C4E1B97712988EC06051B24743A4
jurgenson::21288139D8AE3C6BD0937A334E6621B9
ruuduke::2128D840CEC64B9463339E1DE7FA1DBD
finger::212DB2D2EA55E7973591301C028F7773
voodilina::213035D9BFEDBEC0591D7B318A818E6C
yesterday::235C11EA70D238EF7E73C45F21DA4C0C
nikolajev::235D6BB0833D3A7F35BDEA6CAF8FC340
krissukas::235DC86BC4804EB0959AC165FE55105C
piberman::235E37416423ECDCA68AFE0BCCFB5420
ericcson::235FF178359FFB2B9D7BAE62EA387AA6
boduke::2363697E7A5857A4051257E4E5B5C89C
hunterx::23641EF412C149ED8557A1BDDFABF700
elvira::23660027E76EC5B5F260349C17DCDA07
zzxxcc::2369A47B4F77DE7A3D6E7C23FA123CA7
kiirus::236B845D60B29E1698741B672CDF1A22

```

## Authors

Me :)

