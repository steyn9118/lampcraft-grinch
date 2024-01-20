package steyn91.grinchplugin.Stats;

import org.bukkit.Bukkit;
import steyn91.grinchplugin.GrinchPlugin;

import java.sql.*;

public class Database {

    private static final GrinchPlugin plugin = GrinchPlugin.getPlugin();
    private static Connection connection;

    // Для получения подключения
    public static Connection getConnection() {

        String url = plugin.getConfig().getString("url");
        assert url != null;
        String user = plugin.getConfig().getString("user");
        String pwd = plugin.getConfig().getString("password");

        if (connection == null){
            // Подключение к БД
            try {
                connection = DriverManager.getConnection(url, user, pwd);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            Bukkit.getLogger().info("Подключено к БД");
        }

        try {
            connection.createStatement().execute("SELECT 1");
        } catch (SQLException e) {
            // Подключение к БД
            try {
                connection = DriverManager.getConnection(url, user, pwd);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            Bukkit.getLogger().info("Подключено к БД");
        }
        return connection;
    }

    public static void initDatabase(){
        try {
            Statement statement = getConnection().createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS grinch_stats (playerName varchar(16) primary key, wins int, games int, presentsAll int, presentsOneGame int, lastUpdate DATE)";
            statement.execute(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static PlayerStatsModel getPlayerStat(String playerName){

        try {

            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM grinch_stats WHERE playerName = ?");

            statement.setString(1, playerName);

            ResultSet resultSet = statement.executeQuery();

            PlayerStatsModel statsModel;

            if (resultSet.next()){

                statsModel = new PlayerStatsModel(
                        resultSet.getString("playerName"),
                        resultSet.getInt("wins"),
                        resultSet.getInt("games"),
                        resultSet.getInt("presentsAll"),
                        resultSet.getInt("presentsOneGame"),
                        resultSet.getDate("lastUpdate"));

                statement.close();

                return statsModel;

            }

            statement.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void removePlayerStat(String playerName){

        try {

            PreparedStatement statement = getConnection().prepareStatement("DELETE FROM grinch_stats WHERE playerName = ?");

            statement.setString(1, playerName);

            statement.executeUpdate();
            statement.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void createPlayerStat(String playerName){
        try {

            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO grinch_stats(playerName, wins, games, presentsAll, presentsOneGame, lastUpdate) VALUES (?, ?, ?, ?, ?, ?)");

            statement.setString(1, playerName);
            statement.setInt(2, 0);
            statement.setInt(3, 0);
            statement.setInt(4, 0);
            statement.setInt(5, 0);
            statement.setDate(6, new Date(new java.util.Date().getTime()));

            statement.executeUpdate();
            statement.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public static void updatePlayerStat(PlayerStatsModel statsModel){

        String playerName = statsModel.getPlayerName();

        if (getPlayerStat(playerName) == null){
            createPlayerStat(playerName);
        }

        try {

            PreparedStatement statement = getConnection().prepareStatement("UPDATE grinch_stats SET wins = ?, games = ?, presentsAll = ?, presentsOneGame = ?, lastUpdate = ? WHERE playerName = ?");

            statement.setInt(1, statsModel.getWins());
            statement.setInt(2, statsModel.getGames());
            statement.setInt(3, statsModel.getPresentsAll());
            statement.setInt(4, statsModel.getPresentsOneGame());
            statement.setDate(5, new Date(new java.util.Date().getTime()));
            statement.setString(6, playerName);

            statement.executeUpdate();
            statement.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
