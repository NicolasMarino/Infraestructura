package com.infra;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sistema {
    private Utils utils;

    public Sistema() {
        utils = new Utils();
    }

    public void menu() throws ClassNotFoundException {
        int opcion;
        do {
            Utils.print("\n1. Exclusión mutua \n2. Deadlock \n3. Chequeo de permisos a nivel de programa \n4. Chequeo de permisos a nivel de programa " +
                    "\n5. Ejecución satisfactoria \n6. Tiempo de ejecución  \n7. Fin\n");
            opcion = utils.leerNumeroEntre("Ingrese una opcion", 1, 7, "\033[31mIngrese un número entre 1 y 7\u001B[0m");
            switch (opcion) {
                case 1:
                    mutualExclusion();
                    break;
                case 2:
                    deadlock();
                    break;
                case 3:
                    permissionsProgramCheck();
                    break;
                case 4:
                    permissionsByResourceCheck();
                    break;
                case 5:
                    successfulExecution();
                    break;
                case 6:
                    timeoutExecution();
                    break;
                case 7:
                    System.out.println("Hasta luego!");
                    break;
            }
        } while (opcion != 7);
    }

    private final List<Resource> RESOURCE_LIST = Arrays.asList(new Resource("Printer", false), new Resource("Camera", false),
            new Resource("Monitor", true), new Resource("Speakers", true),
            new Resource("Hdd", true), new Resource("Ram", true));

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
        utils.print("=== Exclusión mutua iniciada ===");

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
            notifyExecutionStatus(printWordProcess, printWordProcess.getTaskById(0), true, user);

            printExcelProcess.run(user);
            notifyExecutionStatus(printExcelProcess, printExcelProcess.getTaskById(0), true, user);

            executeTask(user, printWordProcess, getResourceByName("Ram"), 0);
            executeTask(user, printExcelProcess, getResourceByName("Ram"), 0);

            giveBackResource(printWordProcess, printWordProcess.getTaskById(0));
            giveBackResource(printExcelProcess, printExcelProcess.getTaskById(0));

            executeTask(user, printWordProcess, getResourceByName("Printer"), 1);
            executeTask(user, printExcelProcess, getResourceByName("Printer"), 1);

            printWordProcess.getActualResource().setStatus(Status.AVAILABLE);
            printWordProcess.giveBackResource();
            printWordProcess.terminate();
            notifyExecutionStatus(printWordProcess, printWordProcess.getTaskById(1), false, user);

            executeTask(user, printExcelProcess, getResourceByName("Printer"), 1);
            giveBackResource(printExcelProcess, printExcelProcess.getTaskById(1));

            printExcelProcess.giveBackResource();
            printExcelProcess.terminate();
            notifyExecutionStatus(printExcelProcess, printExcelProcess.getTaskById(1), false, user);

        } else {
            killProcess(printWordProcess, printWordTask, "Permisos");
        }
        utils.print("=== Exclusión mutua finalizada ===");
    }

    private void deadlock() {
        utils.print("=== Deadlock iniciado ===");

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
            notifyExecutionStatus(printWordProcess, printWordProcess.getTaskById(0), true, user);

            printExcelProcess.run(user);
            notifyExecutionStatus(printExcelProcess, printExcelProcess.getTaskById(0), true, user);

            executeTask(user, printWordProcess, getResourceByName("Ram"), 0);
            executeTask(user, printExcelProcess, getResourceByName("Ram"), 0);

            giveBackResource(printWordProcess, printWordProcess.getTaskById(0));
            giveBackResource(printExcelProcess, printExcelProcess.getTaskById(0));

            executeTask(user, printWordProcess, getResourceByName("Printer"), 1);
            executeTask(user, printExcelProcess, getResourceByName("Printer"), 1);

            printWordProcess.getActualResource().setStatus(Status.AVAILABLE);
            printWordProcess.giveBackResource();
            printWordProcess.terminate();
            notifyExecutionStatus(printWordProcess, printWordProcess.getTaskById(1), false, user);

            killProcess(printExcelProcess, printExcelProcess.getTaskById(1), "Deadlock");

        } else {
            killProcess(printWordProcess, printWordTask, "Permisos");
        }
        utils.print("=== Deadlock finalizado ===");
    }

    private void permissionsProgramCheck() {
        Utils.print("=== Chequeo de permisos por programa iniciado ===");

        List<Resource> listOfResources = this.RESOURCE_LIST;

        User user = USERS_LIST.stream().filter(u -> "Roberta".equals(u.getName())).findFirst().get();

        Task readWordTask = new Task("Leer archivo word", 1, getResourceByName("Ram"));
        Task printWordTask = new Task("Imprimir archivo word", 2, getResourceByName("Printer"));
        Process printWordProcess = new Process("Imprimir documento word", Status.AVAILABLE, 3, Arrays.asList(readWordTask, printWordTask), Permissions.TO_PRINT);

        Program wordProgram = new Program("Word", Arrays.asList(printWordProcess));

        if (validatePermissions(printWordProcess, wordProgram, user)) {
            printWordProcess.run(user);
            notifyExecutionStatus(printWordProcess, printWordProcess.getTaskById(0), true, user);

            executeTask(user, printWordProcess, getResourceByName("Ram"), 0);

            giveBackResource(printWordProcess, printWordProcess.getTaskById(0));

            executeTask(user, printWordProcess, getResourceByName("Printer"), 1);

            printWordProcess.getActualResource().setStatus(Status.AVAILABLE);
            printWordProcess.giveBackResource();
            printWordProcess.terminate();
            notifyExecutionStatus(printWordProcess, printWordProcess.getTaskById(1), false, user);


        } else {
            killProcess(printWordProcess, printWordTask, "Permisos");

        }
        Utils.print("=== Chequeo de permisos por programa finalizado ===");
    }

    private void permissionsByResourceCheck() {
        Utils.print("=== Chequeo de permisos por recurso iniciado ===");

        List<Resource> listOfResources = this.RESOURCE_LIST;

        User user = USERS_LIST.stream().filter(u -> "Roberta".equals(u.getName())).findFirst().get();

        Task takePictureTask = new Task("Sacar foto", 2, getResourceByName("Camera"));
        Task savePictureTasl = new Task("Abrir WebCamToy", 1, getResourceByName("Ram"));
        Process webCamToyTakePictureProcess = new Process("Take and save picture", Status.AVAILABLE, 3, Arrays.asList(takePictureTask, savePictureTasl), Permissions.READ);

        Program webCamToyProgram = new Program("WebCamToy", Arrays.asList(webCamToyTakePictureProcess));

        if (validatePermissions(webCamToyTakePictureProcess, webCamToyProgram, user)) {
            webCamToyTakePictureProcess.run(user);
        } else {
            killProcess(webCamToyTakePictureProcess, takePictureTask, "Permisos");

        }
        Utils.print("=== Chequeo de permisos por recurso finalizado ===");
    }

    private void successfulExecution() {
        Utils.print("=== Ejecución satisfactoria iniciando ===");

        List<Resource> listOfResources = this.RESOURCE_LIST;

        User user = USERS_LIST.stream().filter(u -> "Roberta".equals(u.getName())).findFirst().get();

        Task viewVideoTask = new Task("Ver video", 1, getResourceByName("Monitor"));
        Task listenVideoTask = new Task("Escuchar video", 2, getResourceByName("Speakers"));
        Process videoPlayerProcess = new Process("Reproducir video", Status.AVAILABLE, 3, Arrays.asList(viewVideoTask, listenVideoTask), Permissions.READ);

        Program videoPlayerProgram = new Program("Video Player", Arrays.asList(videoPlayerProcess));

        if (validatePermissions(videoPlayerProcess, videoPlayerProgram, user)) {
            videoPlayerProcess.run(user);
            notifyExecutionStatus(videoPlayerProcess, videoPlayerProcess.getTaskById(0), true, user);

            executeTask(user, videoPlayerProcess, getResourceByName("Ram"), 0);

            giveBackResource(videoPlayerProcess, videoPlayerProcess.getTaskById(0));

            executeTask(user, videoPlayerProcess, getResourceByName("Printer"), 1);

            giveBackResource(videoPlayerProcess, videoPlayerProcess.getTaskById(1));

            videoPlayerProcess.giveBackResource();
            videoPlayerProcess.terminate();
            notifyExecutionStatus(videoPlayerProcess, videoPlayerProcess.getTaskById(1), false, user);


        } else {
            killProcess(videoPlayerProcess, viewVideoTask, "Permisos");
        }

        Utils.print("=== Ejecución satisfactoria finalizando ===");
    }

    private void timeoutExecution() {
        Utils.print("=== Timeout iniciando ===");

        List<Resource> listOfResources = this.RESOURCE_LIST;

        User user = USERS_LIST.stream().filter(u -> "Roberta".equals(u.getName())).findFirst().get();

        Task viewVideoTask = new Task("Ver video", 1, getResourceByName("Monitor"));
        Task listenVideoTask = new Task("Escuchar video", 2, getResourceByName("Speakers"));
        Process videoPlayerProcess = new Process("Reproducir video", Status.AVAILABLE, 2, Arrays.asList(viewVideoTask, listenVideoTask), Permissions.READ);

        Program videoPlayerProgram = new Program("Video Player", Arrays.asList(videoPlayerProcess));

        if (validatePermissions(videoPlayerProcess, videoPlayerProgram, user)) {
            videoPlayerProcess.run(user);
            notifyExecutionStatus(videoPlayerProcess, videoPlayerProcess.getTaskById(0), true, user);

            executeTask(user, videoPlayerProcess, getResourceByName("Ram"), 0);

            giveBackResource(videoPlayerProcess, videoPlayerProcess.getTaskById(0));

            executeTask(user, videoPlayerProcess, getResourceByName("Printer"), 1);

            videoPlayerProcess.setAvailableTimeout(videoPlayerProcess.getExecutionTimeout());
            executeTask(user, videoPlayerProcess, getResourceByName("Printer"), 1);

            if (videoPlayerProcess.getActualResource() != null) {
                videoPlayerProcess.getActualResource().setStatus(Status.AVAILABLE);
            }
            videoPlayerProcess.giveBackResource();
            videoPlayerProcess.terminate();
            notifyExecutionStatus(videoPlayerProcess, videoPlayerProcess.getTaskById(1), false, user);
        } else {
            killProcess(videoPlayerProcess, viewVideoTask, "Permisos");
        }

        Utils.print("=== Timeout finalizando ===");
    }

    private void executeTask(User user, Process process, Resource resource, Integer taskId) {
        if (isTimeExceeded(process, process.getTaskById(taskId))) {
            if (askAndGivePermissionResource(user, process, process.getTaskById(taskId))) {
                process.setActualResource(process.getTaskById(taskId).getResource());
                process.getTaskById(taskId).getResource().setStatus(Status.RUNNING);
                Utils.print(String.format("Ejecutando tarea %s por el usuario %s.", process.getTaskById(taskId).getName(), user.getName()));
                process.setAvailableTimeout(process.getAvailableTimeout() - process.getTaskById(taskId).getExecutionTime());
            }
        } else {
            Utils.print(String.format("Error de timeout en la tarea %s del proceso %s ejecutado por el usuario %s.", process.getTaskById(taskId).getName(), process.getName(), user.getName()));
        }
    }

    private boolean isTimeExceeded(Process process, Task task) {
        if (!(process.getAvailableTimeout() - task.getExecutionTime() < 0)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean askAndGivePermissionResource(User user, Process process, Task task) {
        Utils.print(String.format("Usuario %s pide acceso al recurso %s para la tarea %s.", user.getName(), task.getResource().getName(), task.getName()));
        if (process.getActualResource() == null && task.getResource().isAvailable()) {
            Utils.print(String.format("Usuario %s obtiene acceso al recurso %s para la tarea %s.", user.getName(), task.getResource().getName(), task.getName()));
            return true;
        } else {
            Utils.print(String.format("Usuario %s no puede acceso al recurso %s dado el proceso se encuentra utilizando el mismo.", user.getName(), task.getResource().getName()));
            return false;
        }
    }

    private void giveBackResource(Process process, Task task) {
        process.giveBackResource();
        task.getResource().setStatus(Status.AVAILABLE);
        Utils.print(String.format("El proceso %s terminó de ejecutar la tarea %s.", process.getName(), task.getName()));
    }

    private void notifyExecutionStatus(Process process, Task task, Boolean starting, User user) {
        String word = "terminó de";
        if (starting) {
            word = "empezó a";
        }
        if (process.getActualResource() == null) {
            Utils.print(String.format("El proceso %s %s ejecutar la tarea %s por el usuario %s.", process.getName(), word, task.getName(), user.getName()));
        }
    }

    private Resource getResourceByName(String name) {
        return RESOURCE_LIST.stream().filter(e -> name.equals(e.getName())).findFirst().get();
    }

    private boolean validatePermissions(Process process, Program program, User user) {
        boolean isValid = true;
        if (process.validateActionPermission(user)) {
            String errorMessageResource = process.validateResourcesPermission(user).get(false);
            if (errorMessageResource == null) {
                Utils.print(String.format("Se crea el proceso %s referido al programa %s pertenenciente al usuario %s.", process.getName(), program.getName(),
                        user.getName()));
            } else {
                Utils.print(String.format("El usuario %s no tiene permisos sobre el recurso %s al ejecutar el proceso %s en el marco del programa %s.", user.getName(), errorMessageResource, process.getName(), program.getName()));
                isValid = false;
            }
        } else {
            Utils.print(String.format("El usuario %s no tiene permisos sobre el proceso %s en el marco del programa %s..", user.getName(), process.getName(), program.getName()));
            isValid = false;
        }
        return isValid;
    }

    private void killProcess(Process process, Task task, String reason) {
        process.terminate();
        Utils.print(String.format("El proceso %s no pudo ejecutar la tarea (%s) %s, por lo que fue cancelado.", process.getName(), reason, task.getName()));
    }
}
