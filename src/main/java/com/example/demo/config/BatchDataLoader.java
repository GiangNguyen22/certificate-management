
 
package com.example.demo.config;

import com.example.demo.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class BatchDataLoader implements CommandLineRunner {

    private final EntityManager em;

    public BatchDataLoader(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        insertDepartments();
        insertPermissionsAndRoles();
        insertRequestTypesAndFlows();
        insertUsersStaffStudents();
        insertStudentFilesAndRequests();
        insertCertificates();
        insertUserPublicKeys();
    }

    private void insertDepartments() {
        Long cnt = em.createQuery("select count(d) from Department d", Long.class).getSingleResult();
        if (cnt == 0) {
            Department d1 = new Department();
            d1.setName("Computer Science");
            d1.setType("faculty");
            d1.setParent_id(0);
            em.persist(d1);

            Department d2 = new Department();
            d2.setName("Administration");
            d2.setType("office");
            d2.setParent_id(0);
            em.persist(d2);
        }
    }

    private void insertPermissionsAndRoles() {
        Long pCnt = em.createQuery("select count(p) from Permission p", Long.class).getSingleResult();
        if (pCnt == 0) {
            Permission pCreate = new Permission();
            pCreate.setCode("CERT_CREATE");
            pCreate.setDescription("Create certificate");
            em.persist(pCreate);

            Permission pView = new Permission();
            pView.setCode("CERT_VIEW");
            pView.setDescription("View certificate");
            em.persist(pView);

            Permission pAdmin = new Permission();
            pAdmin.setCode("ADMIN");
            pAdmin.setDescription("Full admin");
            em.persist(pAdmin);

            // Roles
            Role rAdmin = new Role();
            rAdmin.setName("ROLE_ADMIN");
            rAdmin.setDescription("Administrator role");
            Set<Permission> adminPerms = new HashSet<>();
            adminPerms.add(pCreate);
            adminPerms.add(pView);
            adminPerms.add(pAdmin);
            rAdmin.setPermissions(adminPerms);
            em.persist(rAdmin);

            Role rStaff = new Role();
            rStaff.setName("ROLE_STAFF");
            rStaff.setDescription("Staff role");
            Set<Permission> staffPerms = new HashSet<>();
            staffPerms.add(pCreate);
            staffPerms.add(pView);
            rStaff.setPermissions(staffPerms);
            em.persist(rStaff);

            Role rStudent = new Role();
            rStudent.setName("ROLE_STUDENT");
            rStudent.setDescription("Student role");
            Set<Permission> studentPerms = new HashSet<>();
            studentPerms.add(pView);
            rStudent.setPermissions(studentPerms);
            em.persist(rStudent);

        }
    }

    private void insertRequestTypesAndFlows() {
        Long cnt = em.createQuery("select count(rt) from RequestType rt", Long.class).getSingleResult();
        if (cnt == 0) {
            RequestType rt1 = new RequestType();
            rt1.setName("Certificate Issuance");
            rt1.setDescription("Request to issue certificate");
            em.persist(rt1);

            RequestFlow f1 = new RequestFlow();
            f1.setAction("SUBMIT");
            f1.setComment("Student submitted request");
            f1.setCreatedAt(LocalDateTime.now().minusDays(2));
            em.persist(f1);

            RequestFlow f2 = new RequestFlow();
            f2.setAction("APPROVE");
            f2.setComment("Admin approved");
            f2.setCreatedAt(LocalDateTime.now().minusDays(1));
            em.persist(f2);
        }
    }

    private void insertUsersStaffStudents() {
        Long uCnt = em.createQuery("select count(u) from Staff u", Long.class).getSingleResult();
        if (uCnt == 0) {
            // Staff (admin)
            Staff admin = new Staff();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setFullName("System Administrator");
            admin.setEmail("admin@example.com");
            admin.setPhone("0123456789");
            admin.setDob(LocalDate.of(1990,1,1));
            admin.setStatus(true);
            admin.setDepartmentId(2);
            admin.setName("Admin Name");
            admin.setStaffCode("STF001");
            // assign role admin
            TypedQuery<Role> q = em.createQuery("select r from Role r where r.name = :name", Role.class);
            q.setParameter("name", "ROLE_ADMIN");
            Role adminRole = q.getResultStream().findFirst().orElse(null);
            if (adminRole != null) {
                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                admin.setRoles(roles);
            }
            em.persist(admin);

            // Students
            Student s1 = new Student();
            s1.setUsername("student1");
            s1.setPassword("changeit");
            s1.setFullName("Nguyễn Văn A");
            s1.setEmail("nguyenvana@example.com");
            s1.setPhone("0987000001");
            s1.setDob(LocalDate.of(2000, 5, 20));
            s1.setStatus(true);
            s1.setDepartmentId(1);
            s1.setStudentCode("STU001");
            s1.setMajorName("Computer Science");
            s1.setClassName("CS21");
            s1.setStartYear("2021");
            s1.setGpa(3.5);
            s1.setPassedEnglish(true);
            s1.setStatusSV("GRADUATED");
            TypedQuery<Role> q2 = em.createQuery("select r from Role r where r.name = :name", Role.class);
            q2.setParameter("name", "ROLE_STUDENT");
            Role studentRole = q2.getResultStream().findFirst().orElse(null);
            if (studentRole != null) {
                Set<Role> roles = new HashSet<>();
                roles.add(studentRole);
                s1.setRoles(roles);
            }
            em.persist(s1);

            Student s2 = new Student();
            s2.setUsername("student2");
            s2.setPassword("changeit");
            s2.setFullName("Trần Thị B");
            s2.setEmail("tranthib@example.com");
            s2.setPhone("0987000002");
            s2.setDob(LocalDate.of(2001, 3, 15));
            s2.setStatus(true);
            s2.setDepartmentId(1);
            s2.setStudentCode("STU002");
            s2.setMajorName("Business");
            s2.setClassName("BM22");
            s2.setStartYear("2022");
            s2.setGpa(3.5);
            s2.setPassedEnglish(false);
            s2.setStatusSV("NOT_ELIGIBLE_ENGLISH");
            if (studentRole != null) {
                Set<Role> roles = new HashSet<>();
                roles.add(studentRole);
                s2.setRoles(roles);
            }
            em.persist(s2);
        }
    }

    private void insertStudentFilesAndRequests() {
        Long cnt = em.createQuery("select count(sf) from StudentFile sf", Long.class).getSingleResult();
        if (cnt == 0) {
            StudentFile sf1 = new StudentFile();
            sf1.setFileName("transcript_STU001.pdf");
            sf1.setFilePath("/files/transcript_STU001.pdf");
            sf1.setSigned(false);
            sf1.setIssueAt(LocalDateTime.now().minusMonths(1));
            em.persist(sf1);

            // create a request for STU001
            TypedQuery<Student> q = em.createQuery("select s from Student s where s.studentCode = :code", Student.class);
            q.setParameter("code", "STU001");
            Student s = q.getResultStream().findFirst().orElse(null);
            if (s != null) {
                StudentRequest req = new StudentRequest();
                req.setDescription("Issue graduation certificate");
                req.setFilePath("/requests/request_STU001.pdf");
                req.setStatus("SUBMITTED");
                req.setCreatedAt(LocalDateTime.now().minusDays(2));
                req.setStudent(s);
                em.persist(req);
            }
        }
    }

    private void insertCertificates() {
        Long cnt = em.createQuery("select count(c) from Certificate c", Long.class).getSingleResult();
        if (cnt == 0) {
            TypedQuery<Student> q = em.createQuery("select s from Student s where s.studentCode = :code", Student.class);
            q.setParameter("code", "STU001");
            Student s = q.getResultStream().findFirst().orElse(null);
            if (s != null) {
                Certificate c = new Certificate();
                c.setCertId("CERT-STU001-0001");
                c.setTemplateId("TEMPLATE_DEFAULT");
                // store the owner user id
                if (s.getId() != null) {
                    c.setUserId(s.getId());
                }
                c.setIssued_at(LocalDateTime.now().minusDays(1).toString());
                c.setExpire_at(LocalDateTime.now().plusYears(1).toString());
                c.setStatus("ACTIVE");
                c.setSerial_no("2025_IT_0001");
                c.setCertificate(null); // omit actual p12
                c.setAlias(s.getStudentCode());
                c.setPassword("changeit");
                c.setPdf_uri("/pdfs/cert_STU001.pdf");
                c.setPdf_sha256(null);
                em.persist(c);
            }
        }
    }

   private void insertUserPublicKeys() {
    Long cnt = em.createQuery("select count(u) from UserPublicKeys u", Long.class).getSingleResult();
    if (cnt == 0) {
        // ✅ Tạo key công khai cho sinh viên đã có (STU001, STU002)
        TypedQuery<Student> q = em.createQuery(
                "select s from Student s where s.studentCode in (:c1, :c2)",
                Student.class
        );
        q.setParameter("c1", "STU001");
        q.setParameter("c2", "STU002");

        q.getResultStream().forEach(s -> {
            UserPublicKeys upk = new UserPublicKeys();

            // user_id: lưu id nếu có, hoặc fallback về studentCode
            upk.setUserId(s.getId() != null ? String.valueOf(s.getId()) : s.getStudentCode());

            // public_key: tạo chuỗi public key demo
            upk.setPublicKey("MIIBIjANBgkqh...FAKEPUBLICKEY-for-" + s.getStudentCode());

            // thời gian tạo (ISO format)
            upk.setCreatedAt(LocalDateTime.now().toString());

            // ✅ thêm trường mới cryptography_type (ví dụ ECC hoặc RSA)
            upk.setCryptographyType("ECC");

            em.persist(upk);
        });
    }
}

}
