package com.mycompany.kurs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Bathroom implements Serializable{
    
    private static Bathroom instance;

    List<Tube> tubes;
    List<Fitting> fittings;
    List<WaterConsumer> waterConsumers;
    List<WaterSource> waterSources;
    List<Drain> drains;
    Links links;

    private static final long serialVersionUID = 1L;
    
    public Bathroom() {
        this.tubes = new ArrayList<>();
        this.fittings = new ArrayList<>();
        this.waterConsumers = new ArrayList<>();
        this.waterSources = new ArrayList<>();
        this.drains = new ArrayList<>();
        this.links = new Links();
    }
    
    public static Bathroom getInstance(){
        if(instance == null){
            instance = new Bathroom();
        }
        return instance;
    }

    public void addTube(String name, int length, int diameter, Type type) throws Exception {
            Tube tube = new Tube(name, (new TubeBuilder()).changeLength(length).changeDiameter(diameter).changeType(type));
            tubes.add(tube);
    }
    
    public Tube searchTube(String name) throws Exception {
        return tubes.stream().findFirst().filter(i -> i.name.equals(name)).orElseThrow(() -> new Exception("No such pipe"));
        
    }

    public void deleteTube(String name) throws Exception {
        tubes.remove(this.searchTube(name));
        /*try {
            if (this.searchTube(name).outp != null) {
                this.searchTube(name).disconnectFitting();
            }
            if (this.searchTube(name).inp != null) {
                this.searchTube(name).inp.disconnectTube(this.searchTube(name));
            }
            tubes.remove(this.searchTube(name));
        } catch (Exception ex) {
            throw new Exception("Something went wrong");//для проверки
        }*/
    }

    public void addFitting(String name, int inputs, int outputs, Type type) throws Exception {
        if (type.equals(Type.PLUMBING)) {
            Fitting fitting = new Fitting(name, inputs, outputs, Type.PLUMBING);
            fittings.add(fitting);
        } else {
            if (type.equals(Type.SEWER)) {
                Fitting fitting = new Fitting(name, inputs, outputs, Type.SEWER);
                fittings.add(fitting);
            }
        }
    }

    public Fitting searchFitting(String name) throws Exception {
        return fittings.stream().findFirst().filter(i -> i.name.equals(name)).orElseThrow(() -> new Exception("No such fitting"));
    }

    public void deleteFitting(String name) throws Exception {//проверить что будет с соседними трубами
        fittings.remove(this.searchFitting(name));
        /*try {
            this.searchFitting(name).disconnectAll();
            fittings.remove(this.searchFitting(name));
        } catch (Exception ex) {
            throw new Exception("Something went wrong");//для проверки
        }*/
    }

    public void addWaterConsumer(String name, int length, int diameter) throws Exception {
        WaterConsumer tube = new WaterConsumer(name, length, diameter);
        waterConsumers.add(tube);
    }

    public WaterConsumer searchWaterConsumer(String name) throws Exception {
        return waterConsumers.stream().findFirst().filter(i -> i.name.equals(name)).orElseThrow(() -> new Exception("No such water consumer"));
    }

    public void deleteWaterConsumer(String name) throws Exception {//проверить что будет с соседними трубами
        waterConsumers.remove(this.searchWaterConsumer(name));
        /*try {
            if (this.searchWaterConsumer(name).outp != null) {
                this.searchWaterConsumer(name).disconnectSewerFitting();
            }
            if (this.searchWaterConsumer(name).inp != null) {
                this.searchWaterConsumer(name).inp.disconnectWaterConsumer();
            }
            waterConsumers.remove(this.searchWaterConsumer(name));
        } catch (Exception ex) {
            throw new Exception("Something went wrong");//для проверки
        }*/
    }

    public void addWaterSource(String name) throws Exception {
        WaterSource wsource = new WaterSource(name);
        waterSources.add(wsource);
    }
    
    public WaterSource searchWaterSource(String name) throws Exception {
        return waterSources.stream().filter(i -> i.name.equals(name)).findFirst().orElseThrow(() -> new Exception("No such water source"));
    }
    
    public void deleteWaterSource(String name) throws Exception {
        waterSources.remove(this.searchWaterSource(name));
        /*try {
            if (this.searchWaterSource(name).outp != null) {
                this.searchWaterSource(name).disconnectPlumbingFitting();
            }
            waterSources.remove(this.searchWaterSource(name));
        } catch (Exception ex) {
            throw new Exception("Something went wrong");//для проверки
        }*/
    }
    
    public void addDrain(String name) throws Exception {
        Drain drain = new Drain(name);
        drains.add(drain);
    }
    
    public Drain searchDrain(String name) throws Exception {
        return drains.stream().findFirst().filter(i -> i.name.equals(name)).orElseThrow(() -> new Exception("No such drain"));
    }
    
    public void deleteDrain(String name) throws Exception {
        drains.remove(this.searchDrain(name));
        /*try {
            if (this.searchDrain(name).inp != null) {
                this.searchDrain(name).inp.disconnectDrain();
            }
            drains.remove(this.searchDrain(name));
        } catch (Exception ex) {
            throw new Exception("Something went wrong");
        }*/
    }


    public void savePlan(String path) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String text = gson.toJson(this);
        try (FileWriter writer = new FileWriter(path, false)) {
            writer.write(text);
        } catch (IOException ex) {
            System.out.println("Save error");
        }
    }
    
    public void loadPlan(String path) throws IOException, ClassNotFoundException {
        String text = "";
        Scanner in = new Scanner(new File(path));
        while(in.hasNextLine()){
            text += in.nextLine();
        }
        in.close();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Bathroom curr = gson.fromJson(text, Bathroom.class);
        this.tubes = curr.tubes;
        this.fittings = curr.fittings;
        this.waterConsumers = curr.waterConsumers;
        this.waterSources = curr.waterSources;
        this.drains = curr.drains;
        this.links = curr.links; }
}
