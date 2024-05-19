package com.linkode.api_server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Studyroom {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)    @Column(name = "studyroom_id")
    private Long id;

    private String studyRoomName;

    private String studyRoomProfile;

    private String status;

    private LocalDateTime createAt;

    private LocalDateTime modifiedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "studyroom")
    private List<MemberStudyroom> memberStudyroomList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "studyroom")
    private List<Data> dataList= new ArrayList<>();


}
