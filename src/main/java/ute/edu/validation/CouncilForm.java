package ute.edu.validation;

import java.util.ArrayList;
import java.util.List;

/** Chỉ nhận những thông tin được phép nhập khi thành lập hội đồng. */
public class CouncilForm {
    public static CouncilForm fromCouncil(ute.edu.entity.ReviewCouncil council) {
        var form = new CouncilForm();
        form.setCode(council.getCode());
        form.setName(council.getName());
        form.setLocation(council.getLocation());
        form.setPeriodId(council.getRegistrationPeriod() == null ? null : council.getRegistrationPeriod().getId());
        form.setDepartmentId(council.getDepartment() == null ? null : council.getDepartment().getId());
        form.setChairmanId(council.getChairman() == null ? null : council.getChairman().getId());
        form.setSecretaryId(council.getSecretary() == null ? null : council.getSecretary().getId());
        form.setMemberIds(council.getMembers().stream().map(member -> member.getLecturer().getId()).toList());
        return form;
    }

    private String code;
    private String name;
    private String location;
    private Long periodId;
    private Long departmentId;
    private Long chairmanId;
    private Long secretaryId;
    private List<Long> memberIds = new ArrayList<>();

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Long getPeriodId() { return periodId; }
    public void setPeriodId(Long periodId) { this.periodId = periodId; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public Long getChairmanId() { return chairmanId; }
    public void setChairmanId(Long chairmanId) { this.chairmanId = chairmanId; }
    public Long getSecretaryId() { return secretaryId; }
    public void setSecretaryId(Long secretaryId) { this.secretaryId = secretaryId; }
    public List<Long> getMemberIds() { return memberIds; }
    public void setMemberIds(List<Long> memberIds) { this.memberIds = memberIds; }
}
