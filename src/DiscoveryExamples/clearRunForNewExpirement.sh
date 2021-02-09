#!/usr/bin/env bash

rm GPCA_Alarm/*/*LOR*
rm GPCA_Alarm/*/*ROR*
rm GPCA_Alarm/*/gpca*.txt
rm -fr GPCA_Alarm/*/output/*

for f in GPCA_Infusion/Prop1/*LOR*; do rm "$f"; done
for f in GPCA_Infusion/*/*ROR*; do rm "$f"; done

rm GPCA_Infusion/*/infusion*.txt
rm -fr GPCA_Infusion/*/output/*

rm TCAS/*/*LOR*
rm TCAS/*/*ROR*
rm TCAS/*/tcas*.txt
rm -fr TCAS/*/output/*


rm WBS/*/*LOR*
rm WBS/*/*ROR*
rm WBS/*/wbs*.txt
rm -fr WBS/*/output/*
