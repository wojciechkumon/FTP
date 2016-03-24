Stan wysoki: (-3V) - (-15V)
Stan niski: 3V - 15V

NADAJNIK

entity RS_out is
	port(TX : out std_logic;
		 WE : inout std_logic_vector(7 downto 0);
		 START : in std_logic;
		 CLK : in std_logic;
		 RESET : in std_logic;
		 GOTOWY : out std_logic)
end RS_out;

architecture arch_RS_out of RS_out is
begin

	process(CLK, RESET, START)
	begin

		if (RESET = '1') then
			TX <= '1';
			GOTOWY <= '1';
		elsif (CLK'event and CLK = '1') then
			if (START = '1') then	
				GOTOWY <= '0';
				NR_BITU <= (others => '0');
				TX <= '0';
			endif;
			if (NR_BITU < 9) then 
				TX <= WE(0);
				WE <= '1' & WE(7 downto 1);
				NR_BITU <= NR_BITU + 1;
			elsif (NR_BITU = 9) then
				GOTOWY = '1';
				NR_BITU <= NR_BITU + 1;
			endif;
		endif;

	end process;

end arch_RS_out;

