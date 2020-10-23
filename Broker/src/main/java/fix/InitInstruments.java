package fix;

import fix.Broker;
import java.util.ArrayList;

public class InitInstruments {
    Instruments gold = new Instruments();
    Instruments silver = new Instruments();
    Instruments platinum = new Instruments();
    Instruments diamonds = new Instruments();

    public ArrayList<Instruments> setInstruments() {
        this.gold = gold;
        this.silver = silver;
        this.platinum = platinum;
        this.diamonds = diamonds;

        ArrayList<Instruments> assets = new ArrayList<Instruments>();

        gold.setName("gold");
        gold.setQuantity(0);
        assets.add(gold);

        silver.setName("silver");
        silver.setQuantity(0);
        assets.add(silver);

        platinum.setName("platinum");
        platinum.setQuantity(0);
        assets.add(platinum);

        diamonds.setName("diamonds");
        diamonds.setQuantity(0);
        assets.add(diamonds);

        return assets;
    }
}
