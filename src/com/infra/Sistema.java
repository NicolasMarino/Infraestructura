package com.infra;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Sistema {
    private Utils utils;

    public Sistema() {
        utils = new Utils();
    }

    public void menu() throws ClassNotFoundException {
        int opcion;
        do {
            utils.print("\n1. Exclusión mutua 1\n2. Opcion 2\n3. Opcion 3\n4. Opcion 4\n5. Fin\n");
            opcion = utils.leerNumeroEntre("Ingrese una opcion", 1, 5, "\033[31mIngrese un número entre 1 y 4\u001B[0m");
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
    /*
    Permisos sobre procesos:
        Admin: Read,Write,To_print
    `   User: Read, To_print
		Guest: Read.

		Permisos sobre Recursos:
		Admin: Impresora, Cámara, Monitor, Speakers, Hdd.
    User: Monitor, Impresora
		Guest: Monitor, Speakers.
     */

    private final List<Resource> RESOURCE_LIST = Arrays.asList(new Resource("Printer"), new Resource("Camara"), new Resource("Monitor"),
            new Resource("Speakers"), new Resource("Hdd"));

    private final List<Role> ROLE_LIST = Arrays.asList(
            new Role("Admin",  RESOURCE_LIST),
            new Role("User", RESOURCE_LIST.stream().filter(e -> "Monitor".equals(e.getName()) || "Printer".equals(e.getName())).collect(Collectors.toList())),
            new Role("Guest", RESOURCE_LIST.stream().filter(e -> "Monitor".equals(e.getName()) || "Speakers".equals(e.getName())).collect(Collectors.toList())));

    //Proceso sobre impresora
    //Rol\
    private final List<User> USERS_LIST = Arrays.asList(
            new User("Santiago", "Santiago123", ROLE_LIST.stream().filter(r -> "Admin".equals(r.getName())).findFirst().get()),
            new User("Pedro", "Pedro53", ROLE_LIST.stream().filter(r -> "User".equals(r.getName())).findFirst().get()),
            new User("Roberta", "Roberta343", ROLE_LIST.stream().filter(r -> "Guest".equals(r.getName())).findFirst().get())
            );

    private void mutualExclusion(){
        utils.print("Exclusión mutua iniciando...");
        // 2 procesos (programas) que sus tareas accedan al mismo recurso.
            /*
                tarea 1 tiempo ejecucion 2.
                --
                tarea 1 usa impresora
                tarea 2 no puede usar impresora, esta bloqueada.
                --
                tarea 1 usa impresora
                tarea 1 deja de usar impresora.
                tarea 1 realizada.
                tarea 2 uso impresora.
                --
                tarae 2 deja usar impresora.
                tarea 2 finalizada.
             */
        // Proceso1: Imprimir documento word
        //     Task 1: Leer archivo word, 1, Ram.
        //     Task 2: Imprimir archivo word, 2, Impresora.
        // Proceso 2: Imprimir documento excel
        //     Task 1: Leer archivo excel, 1, Ram.
        //     Task 2: Imprimir archivo excel, 2, Impresora.
        List<Resource> listOfResources = this.RESOURCE_LIST;

        User user = USERS_LIST.stream().filter(u -> "Santiago".equals(u.getName())).findFirst().get();

        Task task1Process1 = new Task("Leer archivo word",1, listOfResources.stream().filter(e -> "Ram".equals(e.getName())).findFirst().get());
        Task task2Process1 = new Task("Imprimir archivo word",2, listOfResources.stream().filter(e -> "Impresora".equals(e.getName())).findFirst().get());
        Process process = new Process("Imprimir documento word", Status.AVAILABLE, 3, Arrays.asList(task1Process1,task2Process1),Permissions.TO_PRINT);

        Task task1Process2 = new Task("Leer archivo excel",2, listOfResources.stream().filter(e -> "Ram".equals(e.getName())).findFirst().get());
        Task task2Process2 = new Task("Imprimir archivo excel",3, listOfResources.stream().filter(e -> "Impresora".equals(e.getName())).findFirst().get());
        Process process2 = new Process("Imprimir documento excel", Status.AVAILABLE, 5, Arrays.asList(task1Process2,task2Process2),Permissions.TO_PRINT);


        process.run(user);
        process2.run(user);

        if(process.validateActionPermission(user)){
            if(process.validateResourcesPermission(user)){

            }else{

            }
        }else{
            Utils.print(String.format("El usuario %s no tiene permisos sobre el recurso %s", user.getName(), process.getName()));
        }

        process.

    }
}
