package practice_20;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

public class CalculatorView {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalculatorView());
    }

    // Объявление основных компонентов интерфейса
    private JFrame frame;
    private JTextField inputField;
    private JButton[] numberButtons;
    private JButton[] operatorButtons;
    private JButton dotButton, calculateButton;
    private CalculatorModel model;

    // Флаги для отслеживания последнего ввода
    private boolean isLastInputOperator;
    private boolean isLastInputDot;
    private boolean isLastInputEquals;

    // Конструктор класса CalculatorView
    public CalculatorView() {
        model = new CalculatorModel(); // Создание экземпляра CalculatorModel

        frame = new JFrame("Калькулятор"); // Создание окна приложения
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Закрытие приложения при нажатии на крестик
        frame.setSize(300, 400); // Установка размеров окна

        inputField = new JTextField(); // Создание текстового поля для ввода
        inputField.setEditable(false); // Запрет редактирования поля вручную
        frame.add(inputField, BorderLayout.NORTH); // Добавление поля в верхнюю часть окна

        JPanel buttonPanel = new JPanel(); // Создание панели для кнопок
        buttonPanel.setLayout(new GridLayout(4, 4)); // Установка сетки 4x4 для кнопок

        // Создание кнопок для цифр (0-9)
        numberButtons = new JButton[10];
        for (int i = 0; i <= 9; i++) {
            numberButtons[i] = new JButton(String.valueOf(i));
            numberButtons[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    isLastInputDot = false;
                    isLastInputOperator = false;
                    isLastInputEquals = false;
                    inputField.setText(inputField.getText() + e.getActionCommand());
                }
            });
        }

        // Создание кнопок для операторов (+, -, *, /)
        String[] operators = {"/", "*", "-", "+"};
        operatorButtons = new JButton[4];
        for (int i = 0; i <= 3; i++) {
            operatorButtons[i] = new JButton(operators[i]);
            operatorButtons[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (isLastInputOperator || isLastInputDot || inputField.getText().isEmpty()) {
                        return;
                    }
                    isLastInputOperator = true;
                    isLastInputDot = false;
                    isLastInputEquals = false;
                    inputField.setText(inputField.getText() + ' ' + e.getActionCommand() + ' ');
                }
            });
        }

        // Кнопка для точки
        dotButton = new JButton(".");
        dotButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String currentText = inputField.getText();
                if (currentText.endsWith(".") || currentText.endsWith(" ")) {
                    return;
                }
                String[] parts = currentText.split("[+\\-*\\/ ]");
                if (parts[parts.length - 1].contains(".")) {
                    return;
                }
                isLastInputDot = true;
                isLastInputOperator = false;
                isLastInputEquals = false;
                inputField.setText(currentText + e.getActionCommand());
            }
        });

        // Добавление кнопок на панель в определенном порядке
        buttonPanel.add(numberButtons[7]);
        buttonPanel.add(numberButtons[8]);
        buttonPanel.add(numberButtons[9]);
        buttonPanel.add(operatorButtons[0]);
        buttonPanel.add(numberButtons[4]);
        buttonPanel.add(numberButtons[5]);
        buttonPanel.add(numberButtons[6]);
        buttonPanel.add(operatorButtons[1]);
        buttonPanel.add(numberButtons[1]);
        buttonPanel.add(numberButtons[2]);
        buttonPanel.add(numberButtons[3]);
        buttonPanel.add(operatorButtons[2]);
        buttonPanel.add(numberButtons[0]);
        buttonPanel.add(dotButton);
        buttonPanel.add(operatorButtons[3]);

        // Кнопка для вычисления
        calculateButton = new JButton("=");
        calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isLastInputOperator || isLastInputDot || isLastInputEquals) {
                    return;
                }
                try {
                    double result = model.calculateResult(inputField.getText());
                    inputField.setText(Double.toString(result));
                    isLastInputEquals = true;
                } catch (IllegalArgumentException except) {
                    inputField.setText("Ошибка");
                }
            }
        });

        buttonPanel.add(calculateButton); // Добавление кнопки "=" на панель

        frame.add(buttonPanel, BorderLayout.CENTER); // Добавление панели на окно по центру
        frame.setVisible(true); // Отображение окна
    }

    // Внутренний класс CalculatorModel для обработки вычислений
    class CalculatorModel {
        private Stack<Double> numbers; // Стек чисел
        private Stack<String> operators; // Стек операторов

        // Конструктор класса CalculatorModel
        public CalculatorModel() {
            numbers = new Stack<>(); // Инициализация стека чисел
            operators = new Stack<>(); // Инициализация стека операторов
        }

        // Метод для сброса стеков
        public void reset() {
            numbers.clear();
            operators.clear();
        }

        // Метод для вычисления результата математического выражения
        public double calculateResult(String input) {
            String[] tokens = input.split(" "); // Разделение строки на токены

            for (String token : tokens) {
                if (isNumber(token)) {
                    numbers.push(Double.parseDouble(token));
                } else {
                    operators.push(token);
                    resolve(); // Выполнение операции
                }
            }

            while (!operators.isEmpty()) {
                resolve(); // Выполнение оставшихся операций
            }

            if (numbers.size() != 1) {
                throw new IllegalArgumentException("Неверное выражение");
            }
            return numbers.pop(); // Возвращение результата
        }

        // Метод для проверки, является ли строка числом
        private boolean isNumber(String token) {
            try {
                Double.parseDouble(token);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        // Метод для выполнения операции над числами в соответствии с операторами
        private void resolve() {
            if (operators.size() < 1 || numbers.size() < 2)
                return;

            String operator = operators.pop();
            double operand2 = numbers.pop();
            double operand1 = numbers.pop();

            switch (operator) {
                case "+":
                    numbers.push(operand1 + operand2);
                    break;
                case "-":
                    numbers.push(operand1 - operand2);
                    break;
                case "*":
                    numbers.push(operand1 * operand2);
                    break;
                case "/":
                    if (operand2 == 0)
                        throw new IllegalArgumentException("Нельзя делить на ноль");
                    numbers.push(operand1 / operand2);
                    break;
                default:
                    throw new IllegalArgumentException("Неверный оператор");
            }
        }
    }
}
