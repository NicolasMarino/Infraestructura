package com.infra;

import java.util.Arrays;
import java.util.List;
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

    private final List<Resource> RESOURCE_LIST = Arrays.asList(new Resource("Printer"), new Resource("Camera"), new Resource("Monitor"),
            new Resource("Speakers"), new Resource("Hdd"), new Resource("Ram"));

    private final List<Role> ROLE_LIST = Arrays.asList(
            new Role("Admin", Arrays.asList(Permissions.READ, Permissions.TO_PRINT, Permissions.WRITE), RESOURCE_LIST),
            new Role("User", Arrays.asList(Permissions.READ, Permissions.TO_PRINT), RESOURCE_LIST.stream().filter(e -> "Monitor".equals(e.getName()) || "Printer".equals(e.getName())).collect(Collectors.toList())),
            new Role("Guest", Arrays.asList(Permissions.READ), RESOURCE_LIST.stream().filter(e -> "Monitor".equals(e.getName()) || "Speakers".equals(e.getName())).collect(Collectors.toList())));

    private final List<User> USERS_LIST = Arrays.asList(
            new User("Santiago", "Santiago123", ROLE_LIST.stream().filter(r -> "Admin".equals(r.getName())).findFirst().get()),
            new User("Pedro", "Pedro53", ROLE_LIST.stream().filter(r -> "User".equals(r.getName())).findFirst().get()),
            new User("Roberta", "Roberta343", ROLE_LIST.stream().filter(r -> "Guest".equals(r.getName())).findFirst().get())
    );

    private void mutualExclusion() {
        utils.print("Exclusión mutua iniciando...");

        List<Resource> listOfResources = this.RESOURCE_LIST;

        User user = USERS_LIST.stream().filter(u -> "Santiago".equals(u.getName())).findFirst().get();

        Task readWordTask = new Task("Leer archivo word", 1, listOfResources.stream().filter(e -> "Ram".equals(e.getName())).findFirst().get());
        Task printWordTask = new Task("Imprimir archivo word", 2, listOfResources.stream().filter(e -> "Printer".equals(e.getName())).findFirst().get());
        Process printWordDocumentProcess = new Process("Imprimir documento word", Status.AVAILABLE, 3, Arrays.asList(readWordTask, printWordTask), Permissions.TO_PRINT);

        Task readExcelTask = new Task("Leer archivo excel", 2, listOfResources.stream().filter(e -> "Ram".equals(e.getName())).findFirst().get());
        Task printExcelTask = new Task("Imprimir archivo excel", 3, listOfResources.stream().filter(e -> "Printer".equals(e.getName())).findFirst().get());
        Process printExcelDocumentProcess = new Process("Imprimir documento excel", Status.AVAILABLE, 5, Arrays.asList(readExcelTask, printExcelTask), Permissions.TO_PRINT);

        Program wordProgram = new Program("Word", Arrays.asList(printWordDocumentProcess));

        Program excelProgram = new Program("Excel", Arrays.asList(printExcelDocumentProcess));

        printWordDocumentProcess.run(user);
        printExcelDocumentProcess.run(user);

        if(validatePermissions(printWordDocumentProcess, wordProgram, user) && validatePermissions(printExcelDocumentProcess, excelProgram, user)){

        };
    }

    private boolean validatePermissions(Process process, Program program, User user){
        boolean isValid = true;
        if (process.validateActionPermission(user)) {
            if (process.validateResourcesPermission(user)) {
                Utils.print(String.format("Se crea el proceso %s referido al programa %s pertenenciente al usuario %s", process.getName(), program.getName(),
                        user.getName()));
            } else {
                Utils.print(String.format("El usuario %s no tiene permisos sobre el recurso %s", user.getName(), process.getName()));
                isValid = false;
            }
        } else {
            Utils.print(String.format("El usuario %s no tiene permisos sobre el recurso %s", user.getName(), process.getName()));
            isValid = false;
        }
        return isValid;
    }
}
