package com.prodapt.netinsight.deviceInstanceManager;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class CompleteDevice {
    public Device device;
    public ArrayList<Shelf> shelf;
    public ArrayList<Slot> slot;
    public ArrayList<Card> card;
    public ArrayList<CardSlot> cardSlot;
    public ArrayList<Pluggable> pluggable;
    public ArrayList<Port> port;
}
