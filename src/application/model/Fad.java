package application.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * <h2> ----- Attributer ----- </h2>
 * <br>
 * <p1><b>fadNr (int):</b><br>
 * Identifikations nummer for {@code Fad} objektet.</p1><br>
 * <br>
 * <p1><b>fadType (String):</b><br>
 * Typen af {@code Fad} objektet.</p1><br>
 * <br>
 * <p1><b>kapacitetL (double):</b><br>
 * Den totale væskemængde i Liter som {@code Fad} objektet kan indeholde.</p1><br>
 * <br>
 * <p1><b>oprindelse (String):</b><br>
 * Stedet hvorfra dette {@code Fad} objekt stammer fra.</p1><br>
 * <br>
 * <p1><b>nængdeL (double):</b><br>
 * Den nuværende væskemængde i Liter som er lagret i {@code Fad} objektet.</p1><br>
 * <br>
 * <h2> ----- Metoder ----- </h2>
 * <br>
 * <p1><b>addToHistorik (Drinkable drinkable, LocalDate dato):</b><br>
 * Tilføj {@code Drinkable} objekt og tilhørende dato til interne {@code Indholdshistorik}</p1><br>
 * <br>
 * <p1><b>removefromHistorik (Drinkable drinkable, LocalDate dato):</b><br>
 * Henter alle {@code Drinkable} objekter som blev tilføjet på den specifikke dato,
 * og fjerner det specificerede objeckt.</p1><br>
 * <br>
 * <p1><b>fyldPå (...):</b><br>
 * Påfylder indholder fra et andet {@code Fad} eller {@code BundDestillat} objekt til dette objekt
 * og opdatere interne variabler.<br>
 * Metoden er Overloadet - Yderligere information på metodens JavaDoc.</p1><br>
 * <br>
 * <h3>-----------------------------------------------------------</h3>
 */
public class Fad extends Storable {
    //Fadene kan typisk bruges mange gange.
    //Størrelserne på fadene kan variere og Sall ser på anvendelse af mindre fade på 30 og 50 liter, men også på
    //store, som kan indeholde 100, 190 eller 250 liter væske

    private int fadNr;
    private String fadtype;
    private double kapacitetL;
    private String oprindelse;
    private double mængdeL;

    //Linkattributter
    private Map<LocalDate, ArrayList<Drinkable>> indholdshistorik;
    private Destillat fadIndhold;

    /**
     *
     * @param fadNr
     * @param fadtype
     * @param kapacitetL
     * @param oprindelse
     */
    public Fad(int fadNr, String fadtype, double kapacitetL, String oprindelse) {
        this.fadNr = fadNr;
        this.fadtype = fadtype;
        this.kapacitetL = kapacitetL;
        this.oprindelse = oprindelse;
        indholdshistorik = new TreeMap<>();
    }

    /**
     * <p1>Tilføj {@code Drinkable} objekt og tilhørende dato til interne {@code Indholdshistorik}</p1>
     * @param drinkable Objekt af {@code Drinkable} til at tilføje til {@code Indholdshistorik}
     * @param dato Datoen som skal tilføjes til {@code Indholdshistorik}. Sat til dagsdato hvis {@code null}
     * @return {@code void}
     */

    public void addToHistorik(Drinkable drinkable, LocalDate dato) {
        if (dato == null) {
            dato = LocalDate.now();
        }
        ArrayList<Drinkable> indhold = new ArrayList<>();
        if (indholdshistorik.containsKey(dato)) {
            indhold = indholdshistorik.get(dato);
        }

        if (!indhold.contains(drinkable)) {
            indhold.add(drinkable);
            indholdshistorik.put(dato, indhold);
        }
    }

    /**
     * <p1>Henter alle {@code Drinkable} objekter som blev tilføjet på den specifikke dato,
     * og fjerner det specificerede objeckt</p1>
     * @param drinkable Objekt til at fjernes fra {@code Indholdshistorik}
     * @param dato Datoen som anvendes til at hente {@code Drinkable} objekter fra interne liste
     * @return {@code void}
     */
    public void removeFromHistorik(Drinkable drinkable, LocalDate dato) {
        ArrayList<Drinkable> indhold = indholdshistorik.get(dato);
        if (indhold != null) {
            indhold.remove(drinkable);
        }
    }

    private Destillat fyldPå(Destillat destillat, double påhældningsMængde, double kildeMængde, LocalDate dato, String init) {
        if(påhældningsMængde > kildeMængde) {
            throw new IllegalArgumentException("Destillat/Fad har mindre indhold end den ønskede mængde");
        }

        if(påhældningsMængde > kapacitetL - mængdeL) {
            throw new IllegalArgumentException("Fadet har ikke plads til den ønskede mængde");
        }

        if(dato == null) {
            dato = LocalDate.now();
        }

        if(dato.isBefore(destillat.getFærdigDato())) {
            throw new IllegalArgumentException("Påfyldningsdato er før destillatets færdigdato");
        }

        Destillat indholdEfterPåfyldning;
        double originalMængde = mængdeL;
        mængdeL += påhældningsMængde;

        //Hvis indhold er det samme destillat
        if(fadIndhold == null || fadIndhold == destillat) {
            indholdEfterPåfyldning = destillat;
        }
        else {
            // hvis kombi => kombidestillat
            String nytNavn = fadIndhold.getNavn() + "-" + destillat.getNavn();
            indholdEfterPåfyldning = new KombiDestillat(nytNavn, dato, init);
            Maltbatch indholdMalt = fadIndhold.getMaltbatch();
            Maltbatch andetMalt = destillat.getMaltbatch();

            boolean indholdSingle = indholdMalt == Maltbatch.SINGLE_MALT || indholdMalt == Maltbatch.SINGLE_CASK;
            boolean andetSingle = andetMalt == Maltbatch.SINGLE_MALT || andetMalt == Maltbatch.SINGLE_CASK;

            indholdEfterPåfyldning.setMaltbatch(
                    (indholdSingle && andetSingle) ? Maltbatch.SINGLE_MALT : Maltbatch.BLENDED
            );

            ((KombiDestillat) indholdEfterPåfyldning).add(destillat);
            ((KombiDestillat) indholdEfterPåfyldning).add(fadIndhold);

            double alkoholV = originalMængde / 100 * fadIndhold.getAlkoholprocent();
            alkoholV += påhældningsMængde / 100 * destillat.getAlkoholprocent();
            indholdEfterPåfyldning.setAlkoholprocent(alkoholV / mængdeL * 100);
        }

        fadIndhold = indholdEfterPåfyldning;
        addToHistorik(fadIndhold, dato);

        return indholdEfterPåfyldning;
    }

    /**
     * <p1>Påfylder indholder fra et andet {@code Fad} objekt til dette objekt og opdatere interne variabler</p1>
     * @param andetFad Andet {@code Fad} objekt som påfyldes det relaterede Fad.
     * @param mængde Mængden i Liter, som bliver påfyldet dette {@code Fad} objekt.
     * @param dato Datoen registreret for påfyldning. Sat til dagsdato hvis {@code null}
     * @param init Navnet på det nyligt etablerede {@code Destillat} klasse
     * @throws IllegalArgumentException Hvis den givne mængde overskrider {@code Fad} objektets kapacitet
     * @return {@code Destillat}
     */
    public Destillat fyldPåFraFad(Fad andetFad, double mængde, LocalDate dato, String init) {
        Destillat destillat = fyldPå(andetFad.getFadIndhold(), mængde, andetFad.getMængdeL(), dato, init);
        andetFad.setMængdeL(andetFad.getMængdeL() - mængde);
        return destillat;
    }

    /**
     * <p1>Påfylder indholder af {@code BundDestillat} objekt til dette objekt og opdatere interne variabler</p1>
     * @param bundDestillat Det {@code BundDestillat} objket som påfyldes dette Fad.
     * @param mængde Mængden i Liter, som bliver påfyldet dette {@code BundDestillat} objekt.
     * @param dato Datoen registreret for påfyldning. Sat til dagsdato hvis {@code null}
     * @param init Navnet på det nyligt etablerede {@code Destillat} klasse
     * @throws IllegalArgumentException Hvis den givne mængde overskrider {@code Fad} objektets kapacitet
     * @return {@code Destillat}
     */
    public Destillat fyldPåFraDestillat(BundDestillat bundDestillat, double mængde, LocalDate dato, String init) {
        Destillat destillat = fyldPå(bundDestillat, mængde, bundDestillat.getMængdeL(), dato, init);
        bundDestillat.setMængdeL(bundDestillat.getMængdeL() - mængde);

        return destillat;
    }


    public Map<LocalDate, ArrayList<Drinkable>> getIndholdshistorik() {
        return new TreeMap<>(indholdshistorik);
    }

    public Destillat getFadIndhold() {
        return fadIndhold;
    }

    public int getFadNr() {
        return fadNr;
    }

    public String getFadtype() {
        return fadtype;
    }

    public void setFadtype(String fadtype) {
        this.fadtype = fadtype;
    }

    public double getKapacitetL() {
        return kapacitetL;
    }

    public void setKapacitetL(double kapacitetL) {
        this.kapacitetL = kapacitetL;
    }

    public String getOprindelse() {
        return oprindelse;
    }

    public void setOprindelse(String oprindelse) {
        this.oprindelse = oprindelse;
    }

    public double getMængdeL() {
        return mængdeL;
    }

    public void setMængdeL(double mængdeL) {
        this.mængdeL = mængdeL;
    }

    @Override
    public String toString() {
        String destillatString = "Tom";
        if(fadIndhold != null) {
            destillatString = fadIndhold.getNavn();
        }
        return "Fad " + fadNr + " - " + destillatString + "\n" + mængdeL + "L / " + kapacitetL + "L\n" +
                fadtype + " - " + oprindelse;
    }

}
