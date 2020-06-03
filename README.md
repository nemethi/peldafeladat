# A feladatról

A feladat leírása a [feladat.txt](feladat.txt) fájlban olvasható.
A feladathoz mellékelt fájlokon (_OkmanyDTO.java, SzemelyDTO.java, kodszotar21_allampolg.json, 
kodszotar46_okmanytipus.json, arckep_jo.jpg, arckep_rosz.jpg_) igyekeztem semmit sem változtatni —
példának okáért a Java fájlokat az eredeti package-ükben hagytam.

Így többek között a következő hibákat sem javítottam bennük:
* Sem az OkmanyDTO, sem a SzemelyDTO osztályok nem rendelkeznek `equals`, `hashCode` és `toString` metódusokkal.
* A SzemelyDTO osztályban az `aNev` mezőhöz tartozó get és set metódusok nevei nem felelnek meg a név konvenciónak.
* Szintén a SzemelyDTO osztályban az okmánylista az ArrayList implementációt használja, érdemesebb lenne helyette 
  a List interface-t használnia.

# Build

A buildhez a Maven 3.6.3-as verzióját használtam. Ennek hiányában érdemes a mellékelt wrappert használni.
A `verify` parancsot használva a unit tesztek futása és a JAR fájlok összeállítása mellett az integrációs tesztek
is lefutnak.

Windowson:
```cmd
mvnw.cmd verify
```

*nix rendszereken:
```bash
./mvnw verify
```

A unit- és integrációs tesztek coverage reportjai az adott modul
`target/site/jacoco/`, illetve  a `target/site/jacoco-it/` mappákban érhetőek el.

A két microservice futtatható állományai a következő helyeken találhatóak:

`<project root>/comp-okmany/target/comp-okmany-<version>-exec.jar`

`<project root>/comp-szemely/target/comp-szemely-<version>-exec.jar`

## A projekt moduljai

* comp-okmany: a feladatleírásban szereplő 2-es microservice-t valósítja meg. (comp=component)
* comp-szemely: a feladatleírásban szereplő 1-es microservice-t valósítja meg.
* lib-common: más modulok által használt osztályok. (pl: kommunikációra, adattárolásra, stb.)
* pom-coverage: aggregálja más modulok coverage reportjait.
* test-functional: funkcionális tesztek (kvázi acceptance tesztek).

# Amin még lehetne javítani

* Létező validációs frameworköt használni (pl: Java Bean Validation, Spring Validation).
* A validátorokba mozgatni a null és üres értékek ellenőrzését.
* [Defensive copying](http://www.javapractices.com/topic/TopicAction.do?Id=15) használata. Különösen ott, ahol
  collection-ök kerülnek átadásra.
* Az integrációs tesztek és a unit tesztek közé/fölé egy komponens-tesztréteg beiktatása, amely nem a Spring mock context-jét
  használja.
* A funkcionális teszteket beleolvasztani az egyik komponensbe. Így függőség jönne létre a két komponens között, cserébe
  ezekről a tesztekről is lenne coverage.
