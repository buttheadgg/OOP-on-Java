import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import javax.management.StringValueExp;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class UserInterface {
    String[] columnsFilm = new String[]{"ID","Фильм","Режиссер","Год Выпуска","Жанр","Цена(руб.)"};
    String[] columnsCinema = new String[]{"ID","Кинотеатр","Вместительность ","Дата показа", "Сколько билетов продано"};
    String[] columnsSeance = new String[]{"ID","Кинотеатр","Дата показа","Фильм","Цена(руб.)"};
    Base database;

    DefaultTableModel filmModel;
    DefaultTableModel seanceModel;
    DefaultTableModel cinemaModel;

    JFrame frame = new JFrame("CinemaPark");
    JTable cinemaTable;
    JTable seanceTable;
    JScrollPane filmScroll;
    JScrollPane cinemaScroll;
    JScrollPane seanceScroll;
    final JTable filmTable;

    public UserInterface(Base db) {

        database = db;

        frame.setSize(1300, 650);
        frame.setLocation(100, 100);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ImageIcon icon = new ImageIcon("./icons/icon.png");
        frame.setIconImage(icon.getImage());
        JToolBar toolBar = new JToolBar("Панель инструментов");

        JButton edit = new JButton(new ImageIcon("./icons/save.png"));
        edit.setToolTipText("Редактировать");

        JButton upload = new JButton(new ImageIcon("./icons/load.png"));
        upload.setToolTipText("Сеансы");

        JButton add = new JButton(new ImageIcon("./icons/add.png"));
        add.setToolTipText("Добавить");

        JButton delete = new JButton(new ImageIcon("./icons/delete.png"));
        delete.setToolTipText("Удалить");

        JButton info = new JButton(new ImageIcon("./icons/info.png"));
        info.setToolTipText("Информация о программе");

        final JTextField Name = new JTextField("Название фильма");
        JButton search = new JButton("Поиск");


        toolBar.add(edit);
        toolBar.add(upload);
        toolBar.add(add);
        toolBar.add(delete);
        toolBar.add(info);
        toolBar.add(Name);
        toolBar.add(search);
        frame.setLayout(new BorderLayout());
        frame.add(toolBar, "North");

        filmModel = new DefaultTableModel(null, columnsFilm) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        final TableRowSorter<TableModel> sorterFilm = new TableRowSorter(filmModel);
        filmTable = new JTable(filmModel);
        filmTable.setRowSorter(sorterFilm);
        final TableRowSorter<TableModel> filmSorter;
        filmScroll = new JScrollPane(filmTable);
        frame.add(filmScroll, "East");


        cinemaModel = new DefaultTableModel(null, columnsCinema) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cinemaTable = new JTable(cinemaModel);
        final TableRowSorter<TableModel> cinemaSorter;
        cinemaScroll = new JScrollPane(cinemaTable);
        frame.add(cinemaScroll, "Center");


        fillFilmTable();
        fillCinemaTable();
        frame.setVisible(true);

        search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String text = Name.getText();
                if (text.length() == 0) {
                    sorterFilm.setRowFilter((RowFilter)null);
                } else {
                    sorterFilm.setRowFilter(RowFilter.regexFilter(text, new int[0]));
                }

            }
        });

        info.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(frame, "Program name: Cinema\nAuthor: Cherepanov Timofei");
            }
        });

        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                choiceAdd();
            }
        });

        upload.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = cinemaTable.getSelectedRow();
                int id = Integer.parseInt(cinemaTable.getModel().getValueAt(row, 0).toString());
                seances(id);
            }
        });

        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteCinema();
                deleteFilm();
            }

        });
        edit.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = cinemaTable.getSelectedRow();
                int row1 = filmTable.getSelectedRow();
                if(row>-1){
                    int id = Integer.parseInt(cinemaTable.getModel().getValueAt(row, 0).toString());
                    editCinema(id);
                }
                if(row1>-1){
                    int id1= Integer.parseInt(filmTable.getModel().getValueAt(row1, 0).toString());
                    editFilm(id1);
                }
            }
        });

        cinemaTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(!filmTable.contains(e.getPoint())) { // contains(Point point) method is inherited from java.awt.Component
                    filmTable.clearSelection();
                }
            }
        });

        filmTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(!cinemaTable.contains(e.getPoint())) { // contains(Point point) method is inherited from java.awt.Component
                    cinemaTable.clearSelection();   
                }
            }
        });

    }
    private void fillCinemaTable(){
        cinemaModel.setRowCount(0);
        for (Object[] line : database.getCinemaTable()){
            cinemaModel.addRow(line);
        }
    }

    private void fillFilmTable(){
        filmModel.setRowCount(0);
        for (Object[] line : database.getFilmTable()){
            filmModel.addRow(line);
        }
    }

    private void fillSeancesTable(int id){
        seanceModel.setRowCount(0);
        for (Object[] line : database.getSeanceTable(id)){
            seanceModel.addRow(line);
        }
    }

    public void seances(int id)
    {
        JFrame frameS = new JFrame("Сеансы");
        frameS.setSize(600, 350);
        frameS.setLocation(100, 100);
        //frameS.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ImageIcon icon = new ImageIcon("./icons/icon.png");
        frameS.setIconImage(icon.getImage());
        JToolBar toolBar = new JToolBar("Панель инструментов");

        JButton add = new JButton(new ImageIcon("./icons/add.png"));
        add.setToolTipText("Добавить");

        JButton delete = new JButton(new ImageIcon("./icons/delete.png"));
        delete.setToolTipText("Удалить");

        toolBar.add(add);
        toolBar.add(delete);
        frameS.setLayout(new BorderLayout());
        frameS.add(toolBar, "South");

        seanceModel = new DefaultTableModel(null, columnsSeance) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        final TableRowSorter<TableModel> sorterSeance = new TableRowSorter(seanceModel);
        seanceTable = new JTable(seanceModel);
        seanceTable.setRowSorter(sorterSeance);
        final TableRowSorter<TableModel> seanceSorter;
        seanceScroll = new JScrollPane(seanceTable);
        frameS.add(seanceScroll, "North");

        fillSeancesTable(id);
        frameS.setVisible(true);

        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AddSeance(id);
            }
        });
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSeance();
                fillSeancesTable(id);
            }
        });


    }

    public void AddSeance(int id) {
        int idF;
        final JDialog windowAdd = new JDialog(frame, "Add Seance");
        windowAdd.setModal(true);
        windowAdd.setSize(350, 325);
        windowAdd.setLocation(300, 300);
        windowAdd.setResizable(false);
        Container contentPane = windowAdd.getContentPane();
        SpringLayout layout = new SpringLayout();
        contentPane.setLayout(layout);
        Component newCinema = new JLabel("Введите ID фильма для добавления");
        final JTextField cinemaName = new JTextField(5);
        contentPane.add(newCinema);
        contentPane.add(cinemaName);
        layout.putConstraint("West", newCinema, 10, "West", contentPane);
        layout.putConstraint("North", newCinema, 25, "North", contentPane);
        layout.putConstraint("North", cinemaName, 25, "North", contentPane);
        layout.putConstraint("West", cinemaName, 20, "East", newCinema);
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        layout.putConstraint("West", ok, 60, "West", contentPane);
        layout.putConstraint("North", ok, 250, "North", contentPane);
        layout.putConstraint("North", cancel, 250, "North", contentPane);
        layout.putConstraint("West", cancel, 100, "East", ok);
        contentPane.add(ok);
        contentPane.add(cancel);
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Vector<Integer> s = new Vector();
                s.add(Integer.valueOf(cinemaName.getText()));
                int count = 0;
                Iterator var4 = s.iterator();

                while (var4.hasNext()) {
                    Integer s1 = (Integer) var4.next();
                    if (s1> 0 && !s1.equals(" ")) {
                        ++count;
                    }
                }

                if (count == 1) {
                    database.addSeance(s.get(0),id);
                    fillSeancesTable(id);
                    windowAdd.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Заполните все поля!");
                }

            }
        });


        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(windowAdd, "Вы уверены, что хотите закрыть окно?", "Message", 0);
                if (n == 0) {
                    windowAdd.dispose();
                }

            }
        });
        windowAdd.setDefaultCloseOperation(2);
        windowAdd.setVisible(true);
    }


    public void editFilm(int id) {
        String array[] = database.getFilm(id);
        final JDialog windowAdd = new JDialog(frame, "Add Film");
        windowAdd.setModal(true);
        windowAdd.setSize(350, 325);
        windowAdd.setLocation(300, 300);
        windowAdd.setResizable(false);
        Container contentPane = windowAdd.getContentPane();
        SpringLayout layout = new SpringLayout();
        contentPane.setLayout(layout);
        Component newFilm = new JLabel("Название Фильма");
        final JTextField filmName = new JTextField(array[1],15);
        contentPane.add(newFilm);
        contentPane.add(filmName);
        layout.putConstraint("West", newFilm, 10, "West", contentPane);
        layout.putConstraint("North", newFilm, 25, "North", contentPane);
        layout.putConstraint("North", filmName, 25, "North", contentPane);
        layout.putConstraint("West", filmName, 20, "East", newFilm);
        Component director = new JLabel("Режиссер");
        final JTextField newDirector = new JTextField(array[2],15);
        layout.putConstraint("West", director, 10, "West", contentPane);
        layout.putConstraint("North", director, 50, "North", contentPane);
        layout.putConstraint("North", newDirector, 50, "North", contentPane);
        layout.putConstraint("West", newDirector, 20, "East", newFilm);
        contentPane.add(director);
        contentPane.add(newDirector);
        Component year = new JLabel("Год выпуска");
        final JTextField newYear = new JTextField(array[3],15);
        layout.putConstraint("West", year, 10, "West", contentPane);
        layout.putConstraint("North", year, 75, "North", contentPane);
        layout.putConstraint("North", newYear, 75, "North", contentPane);
        layout.putConstraint("West", newYear, 52, "East", year);
        contentPane.add(year);
        contentPane.add(newYear);
        Component genre = new JLabel("Жанр");
        final JTextField newGenre = new JTextField(array[4],15);
        layout.putConstraint("West", genre, 10, "West", contentPane);
        layout.putConstraint("North", genre, 100, "North", contentPane);
        layout.putConstraint("North", newGenre, 100, "North", contentPane);
        layout.putConstraint("West", newGenre, 95, "East", genre);
        contentPane.add(genre);
        contentPane.add(newGenre);
        Component price = new JLabel("Цена");
        final JTextField  newPrice = new JTextField(array[5],15);
        layout.putConstraint("West", price, 10, "West", contentPane);
        layout.putConstraint("North", price, 125, "North", contentPane);
        layout.putConstraint("North", newPrice, 125, "North", contentPane);
        layout.putConstraint("West", newPrice, 97, "East", price);
        contentPane.add(price);
        contentPane.add(newPrice);
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        layout.putConstraint("West", ok, 60, "West", contentPane);
        layout.putConstraint("North", ok, 250, "North", contentPane);
        layout.putConstraint("North", cancel, 250, "North", contentPane);
        layout.putConstraint("West", cancel, 100, "East", ok);
        contentPane.add(ok);
        contentPane.add(cancel);
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Vector<String> s = new Vector();
                s.add(filmName.getText());
                s.add(newDirector.getText());
                s.add(newYear.getText());
                s.add(newGenre.getText());
                s.add(newPrice.getText());
                int count = 0;
                Iterator var4 = s.iterator();

                while (var4.hasNext()) {
                    String s1 = (String) var4.next();
                    if (s1.length() > 0 && !s1.equals(" ")) {
                        ++count;
                    }
                }

                if (count == 5) {
                    database.editFilmTableRow(id, s.get(0), s.get(1), s.get(2), s.get(3),s.get(4));
                    fillFilmTable();
                    windowAdd.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Заполните все поля!");
                }

            }
        });


        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(windowAdd, "Вы уверены, что хотите закрыть окно?", "Message", 0);
                if (n == 0) {
                    windowAdd.dispose();
                }

            }
        });
        windowAdd.setDefaultCloseOperation(2);
        windowAdd.setVisible(true);
    }

    public void editCinema(int id)
    {
        String array[] = database.getCinema(id);
        final JDialog windowAdd = new JDialog(frame, "Edit Cinema");
        windowAdd.setModal(true);
        windowAdd.setSize(350, 325);
        windowAdd.setLocation(300, 300);
        windowAdd.setResizable(false);
        Container contentPane = windowAdd.getContentPane();
        SpringLayout layout = new SpringLayout();
        contentPane.setLayout(layout);
        Component newCinema = new JLabel("Название Кинотетра");
        final JTextField cinemaName = new JTextField(array[1],15);
        contentPane.add(newCinema);
        contentPane.add(cinemaName);
        layout.putConstraint("West", newCinema, 10, "West", contentPane);
        layout.putConstraint("North", newCinema, 25, "North", contentPane);
        layout.putConstraint("North", cinemaName, 25, "North", contentPane);
        layout.putConstraint("West", cinemaName, 20, "East", newCinema);
        Component capacity = new JLabel("Вместительность");
        final JTextField newCapacity = new JTextField(array[2],15);
        layout.putConstraint("West", capacity, 10, "West", contentPane);
        layout.putConstraint("North", capacity, 50, "North", contentPane);
        layout.putConstraint("North", newCapacity, 50, "North", contentPane);
        layout.putConstraint("West", newCapacity, 20, "East", newCinema);
        contentPane.add(capacity);
        contentPane.add(newCapacity);
        Component data = new JLabel("Дата показа");
        final JTextField newData = new JTextField(array[3],15);
        layout.putConstraint("West", data, 10, "West", contentPane);
        layout.putConstraint("North", data, 75, "North", contentPane);
        layout.putConstraint("North", newData, 75, "North", contentPane);
        layout.putConstraint("West", newData, 67, "East", data);
        contentPane.add(data);
        contentPane.add(newData);
        Component sales = new JLabel("Билетов продано");
        final JTextField Ganre = new JTextField(array[4],15);
        layout.putConstraint("West", sales, 10, "West", contentPane);
        layout.putConstraint("North", sales, 100, "North", contentPane);
        layout.putConstraint("North", Ganre, 100, "North", contentPane);
        layout.putConstraint("West", Ganre, 36, "East", sales);
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        layout.putConstraint("West", ok, 60, "West", contentPane);
        layout.putConstraint("North", ok, 250, "North", contentPane);
        layout.putConstraint("North", cancel, 250, "North", contentPane);
        layout.putConstraint("West", cancel, 100, "East", ok);
        contentPane.add(sales);
        contentPane.add(Ganre);
        contentPane.add(ok);
        contentPane.add(cancel);
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Vector<String> s = new Vector();
                s.add(cinemaName.getText());
                s.add(newCapacity.getText());
                s.add(newData.getText());
                s.add(Ganre.getText());
                int count = 0;
                Iterator var4 = s.iterator();

                while (var4.hasNext()) {
                    String s1 = (String) var4.next();
                    if (s1.length() > 0 && !s1.equals(" ")) {
                        ++count;
                    }
                }

                if (count == 4) {
                    database.editCinemaTableRow(id, s.get(0), s.get(1), s.get(2), s.get(3));
                    fillCinemaTable();
                    windowAdd.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Заполните все поля!");
                }

            }
        });


        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(windowAdd, "Вы уверены, что хотите закрыть окно?", "Message", 0);
                if (n == 0) {
                    windowAdd.dispose();
                }

            }
        });
        windowAdd.setDefaultCloseOperation(2);
        windowAdd.setVisible(true);
    }


    public void AddCinema() {
        final JDialog windowAdd = new JDialog(frame, "Add Cinema");
        windowAdd.setModal(true);
        windowAdd.setSize(350, 325);
        windowAdd.setLocation(300, 300);
        windowAdd.setResizable(false);
        Container contentPane = windowAdd.getContentPane();
        SpringLayout layout = new SpringLayout();
        contentPane.setLayout(layout);
        Component newCinema = new JLabel("Название Кинотетра");
        final JTextField cinemaName = new JTextField(15);
        contentPane.add(newCinema);
        contentPane.add(cinemaName);
        layout.putConstraint("West", newCinema, 10, "West", contentPane);
        layout.putConstraint("North", newCinema, 25, "North", contentPane);
        layout.putConstraint("North", cinemaName, 25, "North", contentPane);
        layout.putConstraint("West", cinemaName, 20, "East", newCinema);
        Component capacity = new JLabel("Вместительность");
        final JTextField newCapacity = new JTextField(15);
        layout.putConstraint("West", capacity, 10, "West", contentPane);
        layout.putConstraint("North", capacity, 50, "North", contentPane);
        layout.putConstraint("North", newCapacity, 50, "North", contentPane);
        layout.putConstraint("West", newCapacity, 20, "East", newCinema);
        contentPane.add(capacity);
        contentPane.add(newCapacity);
        Component data = new JLabel("Дата показа");
        final JTextField newData = new JTextField(15);
        layout.putConstraint("West", data, 10, "West", contentPane);
        layout.putConstraint("North", data, 75, "North", contentPane);
        layout.putConstraint("North", newData, 75, "North", contentPane);
        layout.putConstraint("West", newData, 67, "East", data);
        contentPane.add(data);
        contentPane.add(newData);
        Component sales = new JLabel("Билетов продано");
        final JTextField Ganre = new JTextField(15);
        layout.putConstraint("West", sales, 10, "West", contentPane);
        layout.putConstraint("North", sales, 100, "North", contentPane);
        layout.putConstraint("North", Ganre, 100, "North", contentPane);
        layout.putConstraint("West", Ganre, 36, "East", sales);
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        layout.putConstraint("West", ok, 60, "West", contentPane);
        layout.putConstraint("North", ok, 250, "North", contentPane);
        layout.putConstraint("North", cancel, 250, "North", contentPane);
        layout.putConstraint("West", cancel, 100, "East", ok);
        contentPane.add(sales);
        contentPane.add(Ganre);
        contentPane.add(ok);
        contentPane.add(cancel);
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Vector<String> s = new Vector();
                s.add(cinemaName.getText());
                s.add(newCapacity.getText());
                s.add(newData.getText());
                s.add(Ganre.getText());
                int count = 0;
                Iterator var4 = s.iterator();

                while (var4.hasNext()) {
                    String s1 = (String) var4.next();
                    if (s1.length() > 0 && !s1.equals(" ")) {
                        ++count;
                    }
                }

                if (count == 4) {
                    database.addCinema(s.get(0), s.get(1), s.get(2), s.get(3));
                    fillCinemaTable();
                    windowAdd.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Заполните все поля!");
                }

            }
        });


        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(windowAdd, "Вы уверены, что хотите закрыть окно?", "Message", 0);
                if (n == 0) {
                    windowAdd.dispose();
                }

            }
        });
        windowAdd.setDefaultCloseOperation(2);
        windowAdd.setVisible(true);
    }

    public void AddFilm() {
        final JDialog windowAdd = new JDialog(frame, "Add Film");
        windowAdd.setModal(true);
        windowAdd.setSize(350, 325);
        windowAdd.setLocation(300, 300);
        windowAdd.setResizable(false);
        Container contentPane = windowAdd.getContentPane();
        SpringLayout layout = new SpringLayout();
        contentPane.setLayout(layout);
        Component newFilm = new JLabel("Название Фильма");
        final JTextField filmName = new JTextField(15);
        contentPane.add(newFilm);
        contentPane.add(filmName);
        layout.putConstraint("West", newFilm, 10, "West", contentPane);
        layout.putConstraint("North", newFilm, 25, "North", contentPane);
        layout.putConstraint("North", filmName, 25, "North", contentPane);
        layout.putConstraint("West", filmName, 20, "East", newFilm);
        Component director = new JLabel("Режиссер");
        final JTextField newDirector = new JTextField(15);
        layout.putConstraint("West", director, 10, "West", contentPane);
        layout.putConstraint("North", director, 50, "North", contentPane);
        layout.putConstraint("North", newDirector, 50, "North", contentPane);
        layout.putConstraint("West", newDirector, 20, "East", newFilm);
        contentPane.add(director);
        contentPane.add(newDirector);
        Component year = new JLabel("Год выпуска");
        final JTextField newYear = new JTextField(15);
        layout.putConstraint("West", year, 10, "West", contentPane);
        layout.putConstraint("North", year, 75, "North", contentPane);
        layout.putConstraint("North", newYear, 75, "North", contentPane);
        layout.putConstraint("West", newYear, 52, "East", year);
        contentPane.add(year);
        contentPane.add(newYear);
        Component genre = new JLabel("Жанр");
        final JTextField newGenre = new JTextField(15);
        layout.putConstraint("West", genre, 10, "West", contentPane);
        layout.putConstraint("North", genre, 100, "North", contentPane);
        layout.putConstraint("North", newGenre, 100, "North", contentPane);
        layout.putConstraint("West", newGenre, 95, "East", genre);
        contentPane.add(genre);
        contentPane.add(newGenre);
        Component price = new JLabel("Цена");
        final JTextField  newPrice = new JTextField(15);
        layout.putConstraint("West", price, 10, "West", contentPane);
        layout.putConstraint("North", price, 125, "North", contentPane);
        layout.putConstraint("North", newPrice, 125, "North", contentPane);
        layout.putConstraint("West", newPrice, 97, "East", price);
        contentPane.add(price);
        contentPane.add(newPrice);
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        layout.putConstraint("West", ok, 60, "West", contentPane);
        layout.putConstraint("North", ok, 250, "North", contentPane);
        layout.putConstraint("North", cancel, 250, "North", contentPane);
        layout.putConstraint("West", cancel, 100, "East", ok);
        contentPane.add(ok);
        contentPane.add(cancel);
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Vector<String> s = new Vector();
                s.add(filmName.getText());
                s.add(newDirector.getText());
                s.add(newYear.getText());
                s.add(newGenre.getText());
                s.add(newPrice.getText());
                int count = 0;
                Iterator var4 = s.iterator();

                while (var4.hasNext()) {
                    String s1 = (String) var4.next();
                    if (s1.length() > 0 && !s1.equals(" ")) {
                        ++count;
                    }
                }

                if (count == 5) {
                    database.addFilm(s.get(0), s.get(1), s.get(2), s.get(3),s.get(4));
                    fillFilmTable();
                    windowAdd.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Заполните все поля!");
                }

            }
        });


        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(windowAdd, "Вы уверены, что хотите закрыть окно?", "Message", 0);
                if (n == 0) {
                    windowAdd.dispose();
                }

            }
        });
        windowAdd.setDefaultCloseOperation(2);
        windowAdd.setVisible(true);
    }

    public void choiceAdd()
    {
        final JDialog windowAdd = new JDialog(frame, "Choice cinema of film");
        windowAdd.setModal(true);
        windowAdd.setSize(300, 125);
        windowAdd.setLocation(300, 300);
        windowAdd.setResizable(false);
        Container contentPane = windowAdd.getContentPane();
        SpringLayout layout = new SpringLayout();
        contentPane.setLayout(layout);

        JButton cinema = new JButton("Cinema");
        JButton film = new JButton("Film");
        layout.putConstraint("West", cinema, 60, "West", contentPane);
        layout.putConstraint("North", cinema, 35, "North", contentPane);
        layout.putConstraint("North", film, 35, "North", contentPane);
        layout.putConstraint("West", film, 30, "East", cinema);
        contentPane.add(cinema);
        contentPane.add(film);

        cinema.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddCinema();
                windowAdd.dispose();
            }
        });
        film.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddFilm();
                windowAdd.dispose();
            }
        });
        windowAdd.setVisible(true);
    }

    public void deleteCinema() {
        int row = cinemaTable.getSelectedRow();
        if (row!=-1) {
            int id = Integer.parseInt(cinemaTable.getModel().getValueAt(row, 0).toString());
            try {
                database.deleteCinema(id);
                fillCinemaTable();
            } catch (IndexOutOfBoundsException var4) {
                JOptionPane.showMessageDialog(frame, "Таблица пустая!", "Error", 0);
            }
        }
    }
    public void deleteFilm() {
        int row = filmTable.getSelectedRow();
        if (row!=-1) {
            int id = Integer.parseInt(filmTable.getModel().getValueAt(row, 0).toString());
            try {
                database.deleteFilm(id);
                fillFilmTable();
            } catch (IndexOutOfBoundsException var4) {
                JOptionPane.showMessageDialog(frame, "Таблица пустая!", "Error", 0);
            }
        }
    }

    public void deleteSeance() {
        int row = seanceTable.getSelectedRow();
        if (row!=-1) {
            int id = Integer.parseInt(seanceTable.getModel().getValueAt(row, 0).toString());
            try {
                database.deleteSeance(id);
                fillSeancesTable(id);
            } catch (IndexOutOfBoundsException var4) {
                JOptionPane.showMessageDialog(frame, "Таблица пустая!", "Error", 0);
            }
        }
    }

}

    




