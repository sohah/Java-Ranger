#!/usr/bin/env bash

rm GPCA_Alarm/*/*LOR*
rm GPCA_Alarm/*/*ROR*
rm GPCA_Alarm/*/gpca*.txt
rm -fr GPCA_Alarm/*/output/*
rm GPCA_Alarm/*/MultiThread_stats_gpca

for f in GPCA_Infusion/Prop1/*LOR*; do rm "$f"; done
for f in GPCA_Infusion/*/*ROR*; do rm "$f"; done

rm GPCA_Infusion/*/infusion*.txt
rm -fr GPCA_Infusion/*/output/*
rm GPCA_Infusion/*/MultiThread_stats_infusion


rm TCAS/*/*LOR*
rm TCAS/*/*ROR*
rm TCAS/*/tcas*.txt
rm -fr TCAS/*/output/*
rm TCAS/*/MultiThread_stats_tcas


rm WBS/*/*LOR*
rm WBS/*/*ROR*
rm WBS/*/wbs*.txt
rm -fr WBS/*/output/*

rm WBS/*/MultiThread_stats_wbs

