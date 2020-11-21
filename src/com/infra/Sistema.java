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

        Task readWordTask = new Task("Leer archivo word", 1, getResourceByName("Ram"));
        Task printWordTask = new Task("Imprimir archivo word", 2, getResourceByName("Printer"));
        Process printWordProcess = new Process("Imprimir documento word", Status.AVAILABLE, 3, Arrays.asList(readWordTask, printWordTask), Permissions.TO_PRINT);

        Task readExcelTask = new Task("Leer archivo excel", 1, getResourceByName("Ram"));
        Task printExcelTask = new Task("Imprimir archivo excel", 3, getResourceByName("Printer"));
        Process printExcelProcess = new Process("Imprimir documento excel", Status.AVAILABLE, 5, Arrays.asList(readExcelTask, printExcelTask), Permissions.TO_PRINT);

        Program wordProgram = new Program("Word", Arrays.asList(printWordProcess));

        Program excelProgram = new Program("Excel", Arrays.asList(printExcelProcess));


        if (validatePermissions(printWordProcess, wordProgram, user) && validatePermissions(printExcelProcess, excelProgram, user)) {

            printWordProcess.run(user);
            Utils.print(String.format("El usuario %s está ejecutando el proceso %s", user.getName(), printWordProcess.getName()));

            printExcelProcess.run(user);
            Utils.print(String.format("El usuario %s está ejecutando el proceso %s", user.getName(), printExcelProcess.getName()));


            if(askAndGivePermissionResource(user, printWordProcess, getResourceByName("Ram"))){
                printWordProcess.setActualResource(printWordProcess.getTaskById(0).getResource());
                Utils.print(String.format("Ejecutando tarea %s por el usuario %s", printWordProcess.getTaskById(0).getName(), user.getName()));
            };

            if(askAndGivePermissionResource(user, printExcelProcess, getResourceByName("Ram"))){
                printWordProcess.setActualResource(printExcelProcess.getTaskById(0).getResource());
                Utils.print(String.format("Ejecutando tarea %s por el usuario %s", printExcelProcess.getTaskById(0).getName(), user.getName()));
            };

            giveBackResource(printWordProcess,printWordProcess.getTaskById(0));
            giveBackResource(printExcelProcess,printExcelProcess.getTaskById(0));


            if(askAndGivePermissionResource(user, printWordProcess, getResourceByName("Printer"))){
                printWordProcess.setActualResource(printWordProcess.getTaskById(0).getResource());
                printWordProcess.getTaskById(1).getResource().setStatus(Status.RUNNING);
                Utils.print(String.format("Ejecutando tarea %s por el usuario %s", printWordProcess.getTaskById(1).getName(), user.getName()));
            };

            askAndGivePermissionResource(user, printExcelProcess, getResourceByName("Printer"));

            printWordProcess.terminate();
            printExcelProcess.terminate();
        };
    }

    private boolean askAndGivePermissionResource(User user, Process process, Resource resource) {
        Utils.print(String.format("Usuario %s pide acceso al recurso %s", user.getName(), resource.getName()));

        if (process.getActualResource() == null && resource.isAvailable()) {
            Utils.print(String.format("Usuario %s obtiene acceso al recurso %s", user.getName(), resource.getName()));
            return true;
        } else {
            Utils.print(String.format("Usuario %s no puede acceso al recurso %s dado el proceso se encuentra utilizando el mismo.", user.getName(), resource.getName()));
            return false;
        }
    }

    private void giveBackResource(Process process,Task task){
        process.giveBackResource();
        Utils.print(String.format("El proceso %s terminó de ejecutar la tarea %s", process.getName(), task.getName()));
    }

    private Resource getResourceByName(String name){
        return RESOURCE_LIST.stream().filter(e -> name.equals(e.getName())).findFirst().get();
    }

    private boolean validatePermissions(Process process, Program program, User user) {
        boolean isValid = true;
        if (process.validateActionPermission(user)) {
            if (process.validateResourcesPermission(user)) {
                Utils.print(String.format("Se crea el proceso %s referido al programa %s pertenenciente al usuario %s", process.getName(), program.getName(),
                        user.getName()));
            } else {
                Utils.print(String.format("El usuario %s no tiene permisos sobre el recurso.", user.getName()));
                isValid = false;
            }
        } else {
            Utils.print(String.format("El usuario %s no tiene permisos sobre el recurso %s", user.getName(), process.getName()));
            isValid = false;
        }
        return isValid;
    }
}
