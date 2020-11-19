package com.infra;

import java.util.Scanner;

public class Utils {

    private Scanner input;

    public Utils() {
        input = new Scanner(System.in);
    }

    public int leerNumeroEntre(String mensajeSout, int inicioRango, int finRango, String mensajeError) {
        int numero = ingresoNumero(mensajeSout);
        while (!(numero >= inicioRango && numero <= finRango)) {
            numero = ingresoNumero(mensajeError);
        }
        return numero;
    }

    public boolean esNumero(String numero) {
        try {
            Integer.parseInt(numero);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public int ingresoNumero(String mensaje) {
        System.out.println(mensaje);
        String numero = input.nextLine();
        while (!(esNumero(numero))) {
            boolean num = true;
            if (numero.length() >= 10 && num) {
                System.out.println("\033[31mIngrese un numero valido por favor (menos de 10 digitos).\u001B[0m");
                num = false;
            }
            if (num) {
                System.out.println("\033[31mIngrese solo numeros\u001B[0m");

            }
            numero = input.nextLine();
        }
        return Integer.parseInt(numero);
    }
}
