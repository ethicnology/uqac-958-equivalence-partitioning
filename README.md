# uqac-958-equivalence-partitioning

## Devoir: Panique au Kwik-E-Mart
En tant que représentant de l'entreprise NCCR, fabricant de caisses enregisteuses, le gérant du dépanneur Kwik-E-Mart vous appelle de Springfield afin de l'aider à régler un grave problème. La caisse de son dépanneur s'est détraquée et ne donne plus les bons totaux aux clients!

Vous vous rendez sur place et constatez qu'il n'y a qu'une seule caisse au comptoir, et que celle-ci doit être alimentée en rouleaux de papier pour pouvoir être utilisée. **Or, la quantité de rouleaux disponibles est limitée: vous devrez donc être parcimonieux dans les tests que vous choisirez d'exécuter.**

## Contexte
Avant de vous lancer dans les tests, le gérant vous explique le fonctionnement des ventes au Kwik-E-Mart.

- Une liste d'emplettes est un objet **List** dont les éléments sont des objets Item. Un item possède [un code universel de produit (CUP, ou UPC en anglais)](https://fr.wikipedia.org/wiki/Code_universel_des_produits), une courte description textuelle, une quantité et un prix unitaire.
- La caisse est un objet **Register**, dont la méthode statique **print()** qui prend en entrée une liste d'emplette et imprime un reçu de caisse sur un rouleau de papier (**PaperRoll**, dont on reparlera plus tard). La caisse lit et imprime les lignes correspondant aux items un à un, du premier jusqu'au dernier. Elle calcule ensuite le sous-total, applique les taxes, les rabais et les coupons, puis affiche le grand total.
- La caisse vérifie que chaque item ait un **CUP valide**, soit une chaîne de **12 chiffres** dont le dernier, **la clé de contrôle** (check digit), est déterminée par **la valeur des 11 autres**. La classe Upc possède une méthode **getCheckDigit()** qui permet de calculer ce numéro à partir des **11 premiers caractères d'une chaîne**.
- Le dépanneur **ne vend rien dont le prix unitaire est supérieur à 35$**; **la caisse arrête l'impression d'une facture si un des items dépasse ce prix**.
- De la même manière, **les montants négatifs sont également interdits**.
- Des items **peuvent avoir une quantité fractionnaire** --par exemple, des marchandises vendues au poids. Comme le stipule la norme CUP, **ces produits ont toujours un code commençant par 2** et **la caisse rejette les items contrevenant à cette convention**.
- **On ne peut saisir deux fois un item avec le même CUP**. Cependant, **la caisse nous permet de mettre dans la liste un item avec une quantité négative, ce qui a pour effet d'annuler l'ajout antérieur de cet item. Cette opération n'est permise que si l'item à annuler a d'abord été ajouté (i.e. apparaît dans une position antérieure de la liste d'emplettes)**.
- **La caisse rejette les listes d'emplettes vides, et accepte un maximum de 10 entrées (incluant les items à quantité négative)**.

La caisse permet également de gérer certains rabais offerts aux clients. **Ces rabais sont appliqués une fois la liste de tous les items traitée**.

- Si un client achète 5 items distincts (i.e. CUP différents) et que le total avant taxes de ses achats est d'au moins 2$, un rabais de 1$ est appliqué à sa facture.
- Un client peut également faire scanner un ou plusieurs coupons; la caisse les reconnaît à leur CUP, qui commence par 5. On ne peut appliquer qu'un seul coupon avec un CUP donné; le montant du coupon est positif, et il est soustrait du total des achats. Si le total, au moment de traiter le coupon, est inférieur au montant du coupon, celui-ci n'est pas appliqué et la caisse passe au coupon suivant. Les coupons sont autrement soumis aux mêmes règles que les autres items en ce qui concerne les doublons et l'ajout/retrait des emplettes.

L'extrait suivant donne un exemple de coupon de caisse:
```
------------------------------------------
KWIK-E-MART SPRINGFIELD
Mon 04/06/2020 01:30:22

123456789012 Bananas x 1            1.50$
228049185001 Beef 0.50 @  5.75$     2.88$
123456789012 Bananas x -1          -1.50$
647481195995 Chewing gum x 2        1.98$
443482259960 Gobstoppers x 1        0.99$
343234323430 Nerds x 2              2.88$
615193141593 Doritos x 1            1.25$
SUB-TOTAL                           9.98$

Tax SGST 5%                         0.50$
543234323434 Coupon: Doritos Club  -0.50$
Rebate for 5 items                 -1.00$
TOTAL                               8.97$
------------------------------------------
```
On peut voir d'abord la liste des items, avec pour chacun le CUP, la description, la quantité et le prix. Pour les items vendus au poids, le prix unitaire est également affiché après le symbole "@". Dans cette facture, les bananes ajoutées à la ligne 1 sont ensuite retirées à la ligne 3.

Après l'affichage du sous-total, la taxe de Springfield de 5% est ajoutée, suivi de la liste des rabais applicables. Ici, un coupon de 0.50$ a été saisi (sa valeur est positive, mais elle est affichée avec le signe "-" pour montrer qu'il s'agit d'un rabais). Dans ce cas-ci, le client ayant acheté plus de 5 items, un rabais additionnel de 1$ est appliqué. Le total est ensuite affiché.

## Utilisation de la caisse
Le système de la caisse est implémenté sous la forme d'une librairie Java appelée kwikemart-register.jar, fournie avec l'énoncé du devoir. Cette librairie contient également la documentation Javadoc décrivant chaque classe et chaque méthode. Vous pouvez l'ouvrir dans Eclipse en suivant la technique déjà vue.

Dans les fichiers joints à ce devoir se trouve RegisterDemo.java, qui montre un exemple simple d'utilisation de la caisse. On y remarque les éléments suivants:

- Comme le mentionne l'énoncé, il n'y a qu'une seule caisse, et donc une seule instance possible de la classe Register. On obtient cette instance en utilisant la méthode statique getRegister(). Les appels à cette méthode retournent toujours le même objet, et il n'est pas possible de construire de nouvelles instances de la classe Register.
- Pour fonctionner, on doit donner à la caisse un rouleau de papier, soit un objet de la classe PaperRoll, au moyen de la méthode changePaper(). On ne peut construire soi-même d'instances de cette classe: il n'y a que deux rouleaux de papier disponibles, soit les instances désignées par PaperRoll.SMALL_ROLL et PaperRoll.LARGE_ROLL. Le premier permet d'imprimer 25 lignes, et le second 1000 lignes. Une fois le nombre de lignes épuisé, chaque rouleau n'accepte plus que l'on imprime dessus. Comme on ne peut créer de nouvelles instances de ces objets, cela signifie que le nombre de lignes que la caisse peut imprimer pour la durée d'une exécution est limité. On peut le constater dans le programme d'exemple: après que la caisse se soit fait donner le "petit" rouleau, elle peut imprimer la première facture mais lance une OutOfPaperException au moment d'imprimer la seconde.
- Comme on l'a déjà dit, une liste d'emplettes est un objet List contenant des objets de la classe Item. On crée chaque item en utilisant le constructeur, qui prend un code UPC, une description textuelle, une quantité et un prix unitaire. Aucune vérification n'est faite sur la validité des arguments dans le constructeur: ceci n'est fait qu'au moment de l'impression d'une facture.
- C'est la méthode print() de la classe Register qui produit la facture; chacune des lignes du reçu est imprimée sur le rouleau de papier. En imprimant le reçu, si une des conditions mentionnées à la section précédente n'est pas respectée, la caisse lance une exception. Par exemple, InvalidCheckDigitException est lancée si l'un des items de la liste d'emplettes contient un CUP dont la clé de contrôle est invalide.

## Tester la caisse
Étant donné la quantité limitée de papier, vous choisissez d'utiliser la technique de partitionnement en classes d'équivalence (PCE) afin de générer un petit nombre de cas de test représentatifs. Dans ce cas-ci, l'espace des données d'entrée consiste en l'ensemble de toutes les valeurs x que l'on peut donner comme argument à la méthode print(x) (donc des listes d'emplettes). L'objectif du travail n'est pas de trouver des bugs dans la caisse (bien qu'il en reste!), mais plutôt d'utiliser correctement la technique PCE pour choisir les cas de test à appliquer.

Étant donné que vous effectuez des tests de type "boîte noire", voici quelques pistes afin de vous aider à déterminer quelles entrées sont traitées de manière différente par la caisse:

- Avant de commencer, pour bien comprendre le fonctionnement de la caisse, expérimentez l'interaction avec la classe en programmant différents cas de figure manuellement.
- Pensez à toutes les exceptions que peut lancer un appel à print(): ceci inclut les exceptions documentées par la librairie (donc tous les descendants de StoreException), mais aussi toute autre exception que des arguments donnés à la méthode pourraient raisonnablement provoquer. On peut considérer que chaque exception différente correspond à un traitement différent.
- Ne considérez pas que les erreurs: n'oubliez pas que la description du contexte décrit également plusieurs cas de figure valides différents: rabais, coupons, items retirés, etc. 

## Livrable
Sur le site Moodle du cours, remettez:

- Un unique fichier Java, appelé RegisterTest.java, contenant:
    - En commentaire au début du fichier, la liste des classes d'équivalence que vous avez déterminées; si vous avez utilisé une heuristique pour créer une classe, nommez cette heuristique.
    - La suite de tests que vous avez créée. Chacun des cas de test doit être précédé d'un commentaire Javadoc décrivant la combinaison de classes d'équivalence visée par ce test ET le résultat attendu. En suivant la notation des diapositives du cours sur la technique PCE, chaque commentaire représente donc une ligne du grand tableau de tests.
