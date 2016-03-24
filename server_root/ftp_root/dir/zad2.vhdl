entity zad2 is
	port( RESET : in std_logic;
		  CLK : in std_logic;
		  WE1 : in std_logic;
		  WE2 : in std_logic;
		  WYJSCIE : out std_logic_vector(7 downto 0) );
end zad2;


architecture arch_zad2 is

	begin
		process(CLK, RESET)
		begin
			if (RESET = '1') then
				WYJSCIE <= (others => '0');
			elsif (CLK''event and CLK = '0') then
				if (WE1 = '0' and WE2 = '0') then
					WYJSCIE <= WYJSCIE + 1;
				elsif (WE1 = '0' and WE2 = '1') then
					WYJSCIE <= WYJSCIE - 10;
				elsif (WE1 = '1' and WE2 = '0') then
					WYJSCIE <= (others => '0');
				else
					WYJSCIE <= (others => '1');
				end if;
			end if;
		end process;
	end arch_zad2;