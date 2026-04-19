package fr.uge.concurrence.exam.exo1;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Words {
  private static final String LETTERS = "aaaabbccdeeeeeeefghiiijkllmmnnnoopqrrssttuvwxyz";
  private static final List<String> WORDS = List.of("abasourdissement", "abolitionniste", "accessoirement",
      "accompagnement", "accomplissement", "administrateur", "administration", "administrative",
      "affaiblissement", "affectueusement", "agrandissement", "agroalimentaire", "ambitieusement",
      "analogiquement", "analytiquement", "anthropologique", "anthropomorphisme", "anticommunisme",
      "anticommuniste", "anticonstitutionnel", "antimilitariste", "apprivoisement", "approfondissement",
      "approvisionnement", "approvisionner", "approvisionneur", "approximativement", "architectonique",
      "architecturale", "aristocratique", "aristocratiquement", "aristocratisme", "arrondissement",
      "artificiellement", "artistiquement", "assainissement", "assaisonnement", "assouplissement",
      "authentiquement", "autobiographie", "autobiographique", "automatiquement", "automatisation",
      "biotechnologie", "bouleversement", "bureaucratique", "capitalisation", "catastrophique", "centralisation",
      "cinquantenaire", "circonscription", "circonspection", "clandestinement", "classification",
      "collaboratrice", "collectionneur", "collectionneuse", "collectivement", "commercialisation",
      "commercialiser", "commissionnaire", "communicationnel", "comparativement", "condescendance",
      "conditionnement", "confidentielle", "constitutionnel", "constitutionnelle", "continuellement",
      "contradictoire", "contrebalancer", "convenablement", "conventionnelle", "corporellement",
      "correctionnelle", "correspondance", "correspondante", "courageusement", "cristallographie",
      "culturellement", "dactylographie", "dangereusement", "discrimination", "diversification", "divertissement",
      "dramatiquement", "embellissement", "emberlificoter", "emprisonnement", "encorbellement", "engourdissement",
      "enregistrement", "enrichissement", "essentiellement", "ethnographique", "exceptionnelle",
      "exceptionnellement", "excursionniste", "extraordinaire", "fonctionnement", "fondamentalement",
      "franchissement", "gouvernemental", "gouvernementale", "gouvernementaux", "grammaticalement",
      "habituellement", "historiquement", "hypocoristique", "identification", "immanquablement",
      "impersonnellement", "impressionnable", "impressionnant", "impressionnante", "inconditionnel",
      "inconstitutionnel", "incontestablement", "incontournable", "individualiste", "individuellement",
      "industrialisation", "inflationniste", "infrastructure", "inintelligible", "injurieusement",
      "inlassablement", "insatisfaction", "institutionnel", "institutionnelle", "insuffisamment",
      "intellectuelle", "intellectuellement", "intelligentsia", "intensification", "intergouvernementale",
      "internationale", "internationalisation", "internationaux", "interpellation", "interrogatoire",
      "intersyndicale", "invariablement", "investissement", "invraisemblable", "journalistique", "laborieusement",
      "lexicographique", "magnifiquement", "majoritairement", "malheureusement", "marginalisation",
      "minutieusement", "miraculeusement", "multinationale", "multiplication", "nationalisation",
      "obligatoirement", "officiellement", "ostensiblement", "paradoxalement", "parcimonieusement",
      "perfectionnement", "perquisitionner", "personnellement", "pharmaceutique", "philanthropique",
      "philharmonique", "photographique", "ponctuellement", "positionnement", "potentiellement",
      "prestidigitateur", "principalement", "professionnalisme", "professionnelle", "progressivement",
      "proportionnelle", "proportionnellement", "protectionnisme", "protectionniste", "provisoirement",
      "psychanalytique", "psychologiquement", "quotidiennement", "radicalisation", "raisonnablement",
      "rajeunissement", "ralentissement", "rationalisation", "ravitaillement", "recommandation", "reconnaissance",
      "reconstitution", "reconstruction", "redistribution", "refroidissement", "remarquablement",
      "renouvellement", "requalification", "respectivement", "resplendissant", "ressortissante",
      "restructuration", "retentissement", "retransmission", "revalorisation", "rigoureusement",
      "scientifiquement", "scrupuleusement", "sensibilisation", "simplification", "solennellement",
      "somptueusement", "souhaiteraient", "souverainement", "spectaculairement", "substantiellement",
      "successivement", "surendettement", "symboliquement", "tambourinement", "technocratique", "terminologique",
      "traditionnelle", "traditionnellement", "tranquillement", "transformation", "vieillissement",
      "vigoureusement", "volontairement", "vraisemblablement");


  public static boolean isComplete(String state) {
    return !state.contains(".");
  }

  public static String addLetter(char letter, String state, String solution) {
    Objects.requireNonNull(state);
    Objects.requireNonNull(solution);
    var chars = state.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      if (solution.charAt(i) == letter) {
        chars[i] = letter;
      }
    }
    return String.valueOf(chars);
  }

  public static char randomLetter() {
    var index = ThreadLocalRandom.current().nextInt(LETTERS.length());
    return LETTERS.charAt(index);
  }

  public static char randomLetter(String forbidden) {
    Objects.requireNonNull(forbidden);
    var set = forbidden.chars().boxed().collect(Collectors.toSet());
    if (set.size() >= 26) {
      throw new IllegalStateException("You can not forbid that many letters");
    }
    while (true) {
      var index = ThreadLocalRandom.current().nextInt(LETTERS.length());
      var letter = LETTERS.charAt(index);
      if (!set.contains((int) letter)) {
        return letter;
      }
    }
  }

  public static String randomWord() {
    var index = ThreadLocalRandom.current().nextInt(WORDS.size());
    return WORDS.get(index);
  }
}