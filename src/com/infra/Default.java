package com.infra;

public class Default {
    private Utils utils;

    public Default() {
        utils = new Utils();
    }

    public void menu() throws ClassNotFoundException {
        int opcion;
        do {
            System.out.println("\n1. Exclusión mutua 1\n2. Opcion 2\n3. Opcion 3\n4. Opcion 4\n5. Fin\n");
            opcion = utils.leerNumeroEntre("Ingrese una opcion", 1, 4, "\033[31mIngrese un número entre 1 y 4\u001B[0m");
            switch (opcion) {
                case 1:
                    mutualExclusion();
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    System.out.println("Hasta luego!");
                    break;
            }
        } while (opcion != 5);
    }

    private void mutualExclusion(){
        System.out.println("Exclusión mutua iniciando...");

    }
}
