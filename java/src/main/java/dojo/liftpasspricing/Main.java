package dojo.liftpasspricing;

import dojo.liftpasspricing.infrastructure.LiftPassServer;

public final class Main {

    public static void main(String[] args) {
        new LiftPassServer().start(0);
    }
}
