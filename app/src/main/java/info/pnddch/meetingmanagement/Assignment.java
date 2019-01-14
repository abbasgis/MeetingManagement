package info.pnddch.meetingmanagement;

class Assignment {
    // Store the id of the  assignment
    private int id;
    // Store the name of the assignment
    private String name;
    // Store the due date of the assignment
    private String dueDate;
    private String assignDate;
    private String assignTo;
    private boolean is_completed;


    // Constructor that is used to create an instance of the Movie object
    public Assignment(int id, String name, String dueDate, String assignDate, String assignTo, boolean is_completed) {
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
        this.assignDate = assignDate;
        this.assignTo = assignTo;
        this.is_completed = is_completed;
    }

    public int getAssignmentId() {
        return id;
    }

    public void setAssignmentId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(String assignDate) {
        this.assignDate = assignDate;
    }

    public String getAssignTo() {
        return assignTo;
    }

    public void setAssignTo(String assignTo) {
        this.assignTo = assignTo;
    }

    public void setIs_completed(boolean is_completed) {
        this.is_completed = is_completed;
    }

    public boolean isIs_completed() {
        return is_completed;
    }


}
