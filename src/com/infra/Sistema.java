package com.infra;

import javax.rmi.CORBA.Util;
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
            Utils.print("=== Menú principal === \n1. Exclusión mutua \n2. Deadlock \n3. Chequeo de permisos a nivel de programa \n4. Chequeo de permisos a nivel de recursos" +
                    "\n5. Ejecución satisfactoria \n6. Tiempo de ejecución \n7. Scheduller \n8. Fin\n=====================");
            opcion = utils.leerNumeroEntre("Ingrese una opcion:", 1, 8, "\033[31mIngrese un número entre 1 y 8\u001B[0m");
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
                    scheduller();
                    break;
                case 8:
                    System.out.println("Hasta luego!");
                    break;
            }
        } while (opcion != 8);
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
            new User("Roberta", "Roberta343", ROLE_LIST.stream().filter(r -> "Guest".equals(r.getName())).findFirst().get()));

    private void mutualExclusion() {
        Utils.print("=== Exclusión mutua iniciada ===");

        User user = USERS_LIST.stream().filter(u -> "Santiago".equals(u.getName())).findFirst().get();

        Task readWordTask = new Task("leer archivo", 1, getResourceByName("Ram"));
        Task printWordTask = new Task("imprimir archivo", 2, getResourceByName("Printer"));
        Process printWordProcess = new Process("imprimir documento", Status.AVAILABLE, 3, Arrays.asList(readWordTask, printWordTask), Permissions.TO_PRINT);

        Task readExcelTask = new Task("leer archivo", 1, getResourceByName("Ram"));
        Task printExcelTask = new Task("imprimir archivo", 3, getResourceByName("Printer"));
        Process printExcelProcess = new Process("imprimir documento", Status.AVAILABLE, 5, Arrays.asList(readExcelTask, printExcelTask), Permissions.TO_PRINT);

        Program wordProgram = new Program("Word", Arrays.asList(printWordProcess));
        Program excelProgram = new Program("Excel", Arrays.asList(printExcelProcess));


        if (validatePermissions(printWordProcess, wordProgram, user) && validatePermissions(printExcelProcess, excelProgram, user)) {
            printWordProcess.run();
            notifyExecutionStatus(printWordProcess, 0, user, wordProgram);

            printExcelProcess.run();
            notifyExecutionStatus(printExcelProcess, 0, user,excelProgram);

            executeTask(user, printWordProcess, 0, wordProgram);
            executeTask(user, printExcelProcess, 0, excelProgram);

            giveBackResource(printWordProcess, 0, wordProgram);
            giveBackResource(printExcelProcess, 0, excelProgram);

            executeTask(user, printWordProcess, 1, wordProgram);
            executeTask(user, printExcelProcess, 1, excelProgram);

            giveBackResource(printWordProcess, 1, wordProgram);

            executeTask(user, printExcelProcess, 1, excelProgram);
            giveBackResource(printExcelProcess, 1, excelProgram);
        } else {
            killProcess(printWordProcess, printWordTask, "Permisos");
        }
        Utils.print("=== Exclusión mutua finalizada ===");
    }

    private void deadlock() {
        Utils.print("=== Deadlock iniciado ===");

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
            printWordProcess.run();
            notifyExecutionStatus(printWordProcess, 0,user, wordProgram);

            printExcelProcess.run();
            notifyExecutionStatus(printExcelProcess,0, user, excelProgram);

            executeTask(user, printWordProcess, 0, wordProgram);
            executeTask(user, printExcelProcess, 0, excelProgram);

            giveBackResource(printWordProcess, 0, wordProgram);
            giveBackResource(printExcelProcess, 0, excelProgram);

            executeTask(user, printWordProcess, 1, wordProgram);
            executeTask(user, printExcelProcess, 1, excelProgram);

            giveBackResource(printWordProcess, 1, wordProgram);

            killProcess(printExcelProcess, printExcelProcess.getTaskById(1), "Deadlock");

        } else {
            killProcess(printWordProcess, printWordTask, "Permisos");
        }
        utils.print("=== Deadlock finalizado ===");
    }

    private void permissionsProgramCheck() {
        Utils.print("=== Chequeo de permisos por programa iniciado ===");

        User user = USERS_LIST.stream().filter(u -> "Roberta".equals(u.getName())).findFirst().get();

        Task readWordTask = new Task("Leer archivo word", 1, getResourceByName("Ram"));
        Task printWordTask = new Task("Imprimir archivo word", 2, getResourceByName("Printer"));
        Process printWordProcess = new Process("Imprimir documento word", Status.AVAILABLE, 3, Arrays.asList(readWordTask, printWordTask), Permissions.TO_PRINT);

        Program wordProgram = new Program("Word", Arrays.asList(printWordProcess));

        if (validatePermissions(printWordProcess, wordProgram, user)) {
            printWordProcess.run();
            notifyExecutionStatus(printWordProcess, 0, user, wordProgram);

            executeTask(user, printWordProcess, 0, wordProgram);

            giveBackResource(printWordProcess, 0, wordProgram);

            executeTask(user, printWordProcess, 1, wordProgram);

            giveBackResource(printWordProcess, 1, wordProgram);
        } else {
            killProcess(printWordProcess, printWordTask, "Permisos");
        }
        Utils.print("=== Chequeo de permisos por programa finalizado ===");
    }

    private void permissionsByResourceCheck() {
        Utils.print("=== Chequeo de permisos por recurso iniciado ===");

        User user = USERS_LIST.stream().filter(u -> "Roberta".equals(u.getName())).findFirst().get();

        Task takePictureTask = new Task("Sacar foto", 2, getResourceByName("Camera"));
        Task savePictureTask = new Task("Abrir WebCamToy", 1, getResourceByName("Ram"));
        Process webCamToyTakePictureProcess = new Process("Take and save picture", Status.AVAILABLE, 3, Arrays.asList(takePictureTask, savePictureTask), Permissions.READ);

        Program webCamToyProgram = new Program("WebCamToy", Arrays.asList(webCamToyTakePictureProcess));


        if (validatePermissions(webCamToyTakePictureProcess, webCamToyProgram, user)) {
            webCamToyTakePictureProcess.run();
        } else {
            killProcess(webCamToyTakePictureProcess, takePictureTask, "Permisos");

        }
        Utils.print("=== Chequeo de permisos por recurso finalizado ===");
    }

    private void successfulExecution() {
        Utils.print("=== Ejecución satisfactoria iniciando ===");

        User user = USERS_LIST.stream().filter(u -> "Roberta".equals(u.getName())).findFirst().get();

        Task viewVideoTask = new Task("visualizar video", 1, getResourceByName("Monitor"));
        Task listenVideoTask = new Task("escuchar video", 2, getResourceByName("Speakers"));
        Process videoPlayerProcess = new Process("reproducir video", Status.AVAILABLE, 3, Arrays.asList(viewVideoTask, listenVideoTask), Permissions.READ);

        Program videoPlayerProgram = new Program("Video Player", Arrays.asList(videoPlayerProcess));

        if (validatePermissions(videoPlayerProcess, videoPlayerProgram, user)) {
            videoPlayerProcess.run();
            notifyExecutionStatus(videoPlayerProcess, 0, user, videoPlayerProgram);

            executeTask(user, videoPlayerProcess, 0, videoPlayerProgram);

            giveBackResource(videoPlayerProcess, 0, videoPlayerProgram);

            executeTask(user, videoPlayerProcess, 1, videoPlayerProgram);

            giveBackResource(videoPlayerProcess, 1, videoPlayerProgram);

        } else {
            killProcess(videoPlayerProcess, viewVideoTask, "Permisos");
        }

        Utils.print("=== Ejecución satisfactoria finalizando ===");
    }

    private void scheduller() {
        Utils.print("=== Schedulling planificado por tiempo mas corto iniciando ===");

        User user = USERS_LIST.stream().filter(u -> "Santiago".equals(u.getName())).findFirst().get();

        Task writeDocTask = new Task("escribir documento", 4, getResourceByName("Ram"));
        Process writeProcess = new Process("escribir documento", Status.AVAILABLE, 4, Arrays.asList(writeDocTask), Permissions.WRITE);

        Task saveAsDocTask = new Task("guardar documento", 3, getResourceByName("Hdd"));
        Task showWindowTask = new Task("mostrando pantalla guardar como", 2, getResourceByName("Ram"));
        Process saveAsProcess = new Process("guardar como", Status.AVAILABLE, 5, Arrays.asList(saveAsDocTask, showWindowTask), Permissions.WRITE);

        Program notepadProgram = new Program("Bloc de notas", Arrays.asList(writeProcess, saveAsProcess));

        if (validatePermissions(writeProcess, notepadProgram, user) && validatePermissions(saveAsProcess, notepadProgram, user)) {
            writeProcess.sortTaskListByExecutionTime();
            saveAsProcess.sortTaskListByExecutionTime();

            writeProcess.run();
            notifyExecutionStatus(writeProcess, 0, user, notepadProgram);

            executeTask(user, writeProcess, 0, notepadProgram);

            giveBackResource(writeProcess, 0, notepadProgram);

            saveAsProcess.run();
            notifyExecutionStatus(saveAsProcess, 0, user, notepadProgram);

            executeTask(user, saveAsProcess, 0, notepadProgram);

            giveBackResource(saveAsProcess, 0, notepadProgram);

            notifyExecutionStatus(saveAsProcess, 1, user, notepadProgram);

            executeTask(user, saveAsProcess, 1, notepadProgram);

            giveBackResource(saveAsProcess, 1, notepadProgram);

        } else {
            killProcess(writeProcess, writeDocTask, "Permisos");
            killProcess(saveAsProcess, showWindowTask, "Permisos");
        }

        Utils.print("=== Schedulling planificado por tiempo mas corto finalizando ===");
    }

    private void timeoutExecution() {
        Utils.print("=== Timeout iniciando ===");

        User user = USERS_LIST.stream().filter(u -> "Roberta".equals(u.getName())).findFirst().get();

        Task viewVideoTask = new Task("ver video", 1, getResourceByName("Monitor"));
        Task listenVideoTask = new Task("escuchar video", 2, getResourceByName("Speakers"));
        Process videoPlayerProcess = new Process("reproducir video", Status.AVAILABLE, 2, Arrays.asList(viewVideoTask, listenVideoTask), Permissions.READ);

        Program videoPlayerProgram = new Program("Video Player", Arrays.asList(videoPlayerProcess));

        if (validatePermissions(videoPlayerProcess, videoPlayerProgram, user)) {
            videoPlayerProcess.run();
            notifyExecutionStatus(videoPlayerProcess, 0, user, videoPlayerProgram);

            executeTask(user, videoPlayerProcess, 0, videoPlayerProgram);

            giveBackResource(videoPlayerProcess, 0, videoPlayerProgram);

            executeTask(user, videoPlayerProcess, 1, videoPlayerProgram);

            videoPlayerProcess.resetAvailableTimeout();
            executeTask(user, videoPlayerProcess, 1, videoPlayerProgram);

            giveBackResource(videoPlayerProcess, 1, videoPlayerProgram);
        } else {
            killProcess(videoPlayerProcess, viewVideoTask, "Permisos");
        }

        Utils.print("=== Timeout finalizando ===");
    }

    private void executeTask(User user, Process process, Integer taskId, Program program) {
        if (isTimeExceeded(process, process.getTaskById(taskId))) {
            if (askAndGivePermissionResource(user, process, process.getTaskById(taskId), program)) {
                process.setActualResource(process.getTaskById(taskId).getResource());
                process.getResourceByTaskId(taskId).setStatus(Status.RUNNING);
                Utils.print(String.format("Ejecutando tarea %s por el usuario %s en el marco del programa %s.", process.getTaskById(taskId).getName(), user.getName(), program.getName()));
                process.setAvailableTimeout(process.getAvailableTimeout() - process.getTaskById(taskId).getExecutionTime());
            }
        } else {
            Utils.print(String.format("Hubo un rror de timeout al intentar ejecutar la tarea %s del proceso %s ejecutado por el usuario %s en el marco del programa %s.", process.getTaskById(taskId).getName(), process.getName(), user.getName(), program.getName()));
        }
    }

    private boolean isTimeExceeded(Process process, Task task) {
        if (!(process.getAvailableTimeout() - task.getExecutionTime() < 0)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean askAndGivePermissionResource(User user, Process process, Task task, Program program) {
        Utils.print(String.format("Usuario %s pide acceso al recurso %s para la tarea %s en el marco del programa %s.", user.getName(), task.getResource().getName(), task.getName(), program.getName()));
        if (process.getActualResource() == null && task.getResource().isAvailable()) {
            Utils.print(String.format("Usuario %s obtiene acceso al recurso %s para la tarea %s en el marco del programa %s.", user.getName(), task.getResource().getName(), task.getName(), program.getName()));
            return true;
        } else {
            Utils.print(String.format("Usuario %s no puede acceder al recurso %s en el marco del programa %s dado que el mismo se encuentra en uso.", user.getName(), task.getResource().getName(), program.getName()));
            return false;
        }
    }

    private void giveBackResource(Process process, Integer taskId, Program program) {
        process.terminate();
        process.getResourceByTaskId(taskId).setStatus(Status.AVAILABLE);
        Utils.print(String.format("El proceso %s devolvió el recurso %s y terminó de ejecutar la tarea %s en el marco del programa %s.", process.getName(), process.getResourceByTaskId(taskId).getName(), process.getTaskById(taskId).getName(), program.getName()));
    }

    private void notifyExecutionStatus(Process process, Integer taskId, User user, Program program) {
        Utils.print(String.format("El proceso %s empezó a ejecutar la tarea %s por el usuario %s en el marco del programa %s.", process.getName(), process.getTaskById(taskId).getName(), user.getName(), program.getName()));
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
        if(process.getActualResource() != null){
            process.terminate();
        }
        Utils.print(String.format("El proceso %s no pudo ejecutar la tarea (%s) %s, por lo que fue cancelado.", process.getName(), reason, task.getName()));
    }
}
