import nfl_data_py

df = nfl_data_py.import_seasonal_data([2024], s_type="REG")
players_info = nfl_data_py.import_players()

wopr_col = "wopr_x" if "wopr_x" in df.columns else "wopr_y"
top5 = df.nlargest(5, wopr_col)
top5 = top5.merge(players_info[["player_id", "player_name"]], on="player_id", how="left")

print(top5[["player_name", wopr_col]])
