package application.model;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * <h2>----- Attributer -----</h2>
 * <br
 * <p1><b>Destillater ({@code Arraylist<Destillat>}):</b><br>
 * Alle de individuelle {@code Destillat} instanser som {@code KombiDestillat} består af</p1><br>
 * <br>
 * <h2>----- Metoder -----</h2>
 * <br>
 * <p1><b>add (Destillat destillat):</b><br>
 * Tilføj en {@code Destillat} instans til den interne arraylist</p1><br>
 * <br>
 * <p1><b>remove (Destillat destillat):</b><br>
 * Metode til at fjerne et specifikt {@code Destillat} fra den interne liste</p1><br>
 * <br>
 * <p1><b>getChild (int i):</b><br>
 * Returner et instans af {@code Destillat} klassen fra interne arraylist baseret på Destillatets placering</p1><br>
 * <i>Throws</i><br>
 * - ArrayIndexOutOFBoundsException: Hvis index er uden for den interne arraylists kapacitet.<br>
 * <br>
 * <h3>---------------------------------------------</h3>
 */

public class KombiDestillat extends Destillat{
    //Linkattributter
    private ArrayList<Destillat> destillater;

    public KombiDestillat(String navn, LocalDate færdigDato, String init) {
        super(navn,færdigDato, init);

        this.destillater = new ArrayList<>();
    }

    public boolean indeholderDestillat(Destillat destillat) {
        boolean indeholderDestillat = false;

        if(destillater.contains(destillat)) {
            indeholderDestillat = true;
        }
        else {
            int i = 0;
            while(!indeholderDestillat && i < destillater.size()) {
                Destillat d = destillater.get(i);
                if(d instanceof KombiDestillat kd) {
                    indeholderDestillat = kd.indeholderDestillat(destillat);
                }
                i++;
            }
        }
        return indeholderDestillat;
    }



    /**
     * <p1>Metode til at tilføje et {@code Destillat} til den tilhørende {@code KombiDestillat} klasse.
     * Tager højde for om det specificerede Destillat allerede eksisterer i den interne liste</p1>
     * @param destillat Det specifikke {@code Destillat} instans som ønskes tilføjet
     * @return {@code void}
     */
    public void add(Destillat destillat) {
        if(!destillater.contains(destillat)) {
            destillater.add(destillat);
        }
    }

    /**
     * <p1>Fjern et {@code Destillat} fra listen af instanser som {@code KombiDestillat} består af</p1>
     * @param destillat Det specifikke {@code Destillat} instans som ønskes fjernet
     * @return {@code void}
     */
    public void remove(Destillat destillat) {
        destillater.remove(destillat);
    }

    /**
     * <p1>Returner et specifikt {@code Destillat} ud fra det placering i den interne {@code Arraylist}</p1>
     * @param i Placeringen i den interne {@code Arraylist} af det {@code Destillat} som hentes
     * @throws ArrayIndexOutOfBoundsException Hvis placeringen er uden for listens kapacitet
     * @return Destillat
     */
    public Destillat getChild(int i) {
        if(destillater.size() >= i) {
            return destillater.get(i);
        }
        else {
            throw new ArrayIndexOutOfBoundsException("Destillatet består kun af 2");
        }
    }


    @Override
    public String toString() {
        return super.toString() + "\nKombidestillat";
    }
}
