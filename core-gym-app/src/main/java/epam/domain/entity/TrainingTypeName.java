package epam.domain.entity;

public enum TrainingTypeName {

    JAVA("Java"), C("C"), PYTHON("Python"), ANGULAR("Angular"), REACT("React"),
    JAVASCRIPT("Javascript"), TYPESCRIPT("Typescript"), PHP("Php"),
    BA("Business Analyst"), QA("Qa"), DEVOPS("Devops");

    private final String name;

    TrainingTypeName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static TrainingTypeName getByName(String name) {
        TrainingTypeName trainingTypeName = switch (name) {
            case "JAVA" -> TrainingTypeName.JAVA;
            case "C" -> TrainingTypeName.C;
            case "PYTHON" -> TrainingTypeName.PYTHON;
            case "ANGULAR" -> TrainingTypeName.ANGULAR;
            case "REACT" -> TrainingTypeName.REACT;
            case "JAVASCRIPT" -> TrainingTypeName.JAVASCRIPT;
            case "TYPESCRIPT" -> TrainingTypeName.TYPESCRIPT;
            case "PHP" -> TrainingTypeName.PHP;
            case "BA" -> TrainingTypeName.BA;
            case "QA" -> TrainingTypeName.QA;
            default -> TrainingTypeName.DEVOPS;
        };
        return trainingTypeName;
    }

    public static boolean isExists(String t){
        try {
            TrainingTypeName.valueOf(t.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
