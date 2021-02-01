package com.guptagagann.myapplication.entity;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class FlaskApiResponseBody implements Serializable{


    List<String> sortedSentenceList=new ArrayList<>();

    List<String> sortedSentimentList=new ArrayList<>();

    List<String> paras_list=new ArrayList<>();

    Map<String,Integer> tncs_dt=new HashMap<>();

    Map<String ,Integer> rankedDictionary = new HashMap<>();
}
